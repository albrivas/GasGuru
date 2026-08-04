
import com.android.build.api.variant.ApplicationAndroidComponentsExtension
import com.android.build.api.variant.ResValue
import com.android.build.gradle.BaseExtension
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.configure
import java.io.FileNotFoundException
import java.util.Properties

/**
 * Single source of truth for Android application environments and versioning.
 *
 * Responsibilities:
 *   - Reads [versions.properties] from the project root (same file bumped by release pipelines).
 *   - Defines the full list of environments (prod, mock) x build variants (debug, release) and
 *     their Android-specific settings.
 *   - Configures [productFlavors] + applies [versionCode]/[versionName] from properties,
 *     and sets `app_name` per (flavor, buildType) combination via the variant API (build type
 *     resValues otherwise win over flavor resValues, which flattened the matrix to 2 names).
 *
 * iOS keeps its own equivalent — `.xcconfig` files per (environment, buildType) checked into
 * `iosApp/Config/` and maintained by hand — rather than having Gradle generate them. That's the
 * standard KMP pattern: iOS build settings are static and native to Xcode; when a value needs to
 * reach shared Kotlin code, it flows from iOS into Gradle (e.g. `-Pbuildkonfig.flavor=...`), not
 * the other way around. Bump the iOS xcconfig versions by hand alongside `versions.properties`
 * (see the `release` skill).
 *
 * Usage: apply `gasguru.environments` (alias [gasguru-flavors]) to [:app]. No-op on modules
 * without the `com.android.application` plugin.
 */
class EnvironmentsConventionPlugin : Plugin<Project> {

    private data class Environment(
        val name: String,
        val bundleSuffix: String?,
        val nameSuffix: String?,
        val versionNameSuffix: String?,
        val isMock: Boolean,
    )

    private data class BuildVariant(
        val nameSuffix: String?,
        val isDebug: Boolean,
    )

    private val environments = listOf(
        Environment(
            name = "prod",
            bundleSuffix = null,
            nameSuffix = null,
            versionNameSuffix = null,
            isMock = false,
        ),
        Environment(
            name = "mock",
            bundleSuffix = ".mock",
            nameSuffix = " Mock",
            versionNameSuffix = "-mock",
            isMock = true,
        ),
    )

    private val buildVariants = listOf(
        BuildVariant(nameSuffix = " Debug", isDebug = true),
        BuildVariant(nameSuffix = null, isDebug = false),
    )

    override fun apply(project: Project) {
        val versionProps = readVersionProperties(project)
        val versionName = buildVersionName(versionProps)
        val versionCode = versionProps.getProperty("versionCode").trim().toInt()

        configureAndroid(project, versionCode, versionName)
    }

    // ── Android ──────────────────────────────────────────────────────────────

    private fun configureAndroid(
        project: Project,
        versionCode: Int,
        versionName: String,
    ) {
        project.plugins.withId("com.android.application") {
            project.extensions.configure<BaseExtension> {
                defaultConfig {
                    this.versionCode = versionCode
                    this.versionName = versionName
                }
                flavorDimensions("environment")
                productFlavors {
                    environments.forEach { env ->
                        create(env.name) {
                            dimension = "environment"
                            env.bundleSuffix?.let { applicationIdSuffix = it }
                            env.versionNameSuffix?.let { versionNameSuffix = it }
                        }
                    }
                }
            }

            // app_name per (flavor, buildType): resValues declared on buildTypes win over
            // flavor resValues during merge, so a flavor-only resValue can never reach
            // mockRelease. The variant API runs after the merge and can set it directly.
            configureAppNamePerVariant(project)
        }
    }

    private fun configureAppNamePerVariant(project: Project) {
        val androidComponents = project.extensions.getByType(ApplicationAndroidComponentsExtension::class.java)
        androidComponents.onVariants { variant ->
            val env = environments.first { it.name == variant.flavorName }
            val buildVariant = buildVariants.first { it.isDebug == (variant.buildType == "debug") }
            // Mock doesn't distinguish debug/release in the app name (both show "GasGuru Mock") —
            // "GasGuru Mock Debug" exceeds the ~15 character guidance for the Home Screen label
            // and iOS/Android both start stripping spaces before truncating it.
            val debugSuffix = if (env.isMock) null else buildVariant.nameSuffix
            val appName = BASE_APP_NAME + (env.nameSuffix ?: "") + (debugSuffix ?: "")
            variant.resValues.put(
                variant.makeResValueKey("string", "app_name"),
                ResValue(appName),
            )
        }
    }

    // ── Helpers ───────────────────────────────────────────────────────────────

    private fun readVersionProperties(project: Project): Properties {
        val propsFile = project.rootProject.file("versions.properties")
        if (!propsFile.exists()) throw FileNotFoundException("versions.properties not found at ${propsFile.absolutePath}")
        return Properties().apply { propsFile.inputStream().use { load(it) } }
    }

    private fun buildVersionName(props: Properties): String {
        val major = props.getProperty("versionMajor").trim()
        val minor = props.getProperty("versionMinor").trim()
        val patch = props.getProperty("versionPatch").trim()
        return "$major.$minor.$patch"
    }

    private companion object {
        const val BASE_APP_NAME = "GasGuru"
    }
}
