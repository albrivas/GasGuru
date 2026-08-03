
import com.android.build.api.variant.ApplicationAndroidComponentsExtension
import com.android.build.api.variant.ResValue
import com.android.build.gradle.BaseExtension
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.configure
import java.io.FileNotFoundException
import java.util.Properties

/**
 * Single source of truth for application environments and versioning across Android and iOS.
 *
 * Responsibilities:
 *   - Reads [versions.properties] from the project root (same file bumped by release pipelines).
 *   - Defines the full list of environments (prod, mock) x build variants (debug, release) and
 *     their platform-specific settings — same matrix on both platforms.
 *   - Android: configures [productFlavors] + applies [versionCode]/[versionName] from properties,
 *     and sets `app_name` per (flavor, buildType) combination via the variant API (build type
 *     resValues otherwise win over flavor resValues, which flattened the matrix to 2 names).
 *   - iOS: generates one `.xcconfig` per (environment, buildType) pair under [iosApp/Config/] so
 *     Xcode picks the right version, bundle id, name, and [SWIFT_ACTIVE_COMPILATION_CONDITIONS].
 *   - Registers a [generateIosEnvConfig] task that runs before iOS framework builds. Only
 *     registered once, on the KMP module ([:composeApp]) — [:app] is not part of the iOS build.
 *
 * Usage: apply `gasguru.environments` (alias [gasguru-flavors]) to [:app] and [:composeApp].
 * The Android block is a no-op on [:composeApp] (no `com.android.application` plugin there);
 * the iOS block is a no-op on [:app] (no Kotlin Multiplatform plugin there).
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
        /** Capitalized, matches both the iOS Xcode configuration suffix and Kotlin/Native's map. */
        val xcodeSuffix: String,
        val applicationIdSuffix: String?,
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
        BuildVariant(xcodeSuffix = "Debug", applicationIdSuffix = ".debug", nameSuffix = " Debug", isDebug = true),
        BuildVariant(xcodeSuffix = "Release", applicationIdSuffix = null, nameSuffix = null, isDebug = false),
    )

    override fun apply(project: Project) {
        val versionProps = readVersionProperties(project)
        val versionName = buildVersionName(versionProps)
        val versionCode = versionProps.getProperty("versionCode").trim().toInt()

        configureAndroid(project, versionCode, versionName)

        // Registered only on the KMP module: :app has no iOS build to feed, and registering it
        // there too would create a second task writing the same output directory.
        project.plugins.withId("org.jetbrains.kotlin.multiplatform") {
            registerGenerateIosEnvConfigTask(project, versionName, versionCode)
        }
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
            val appName = BASE_APP_NAME + (env.nameSuffix ?: "") + (buildVariant.nameSuffix ?: "")
            variant.resValues.put(
                variant.makeResValueKey("string", "app_name"),
                ResValue(appName),
            )
        }
    }

    // ── iOS xcconfig generation ───────────────────────────────────────────────

    private fun registerGenerateIosEnvConfigTask(
        project: Project,
        versionName: String,
        versionCode: Int,
    ) {
        val versionsFile = project.rootProject.file("versions.properties")
        val configDir = project.rootProject.file("iosApp/Config")

        val generateTask = project.tasks.register("generateIosEnvConfig") {
            group = "gasguru"
            description = "Generates per-environment .xcconfig files for iOS builds."

            // versions.properties is read at configuration time above; declare it as an input
            // so a version bump invalidates the task instead of leaving stale .xcconfig files
            // marked UP-TO-DATE (outputs.dir alone doesn't change when the source file does).
            inputs.file(versionsFile).withPropertyName("versionsProperties")
            inputs.property("versionName", versionName)
            inputs.property("versionCode", versionCode)
            outputs.dir(configDir).withPropertyName("iosConfigDir")

            doLast {
                configDir.mkdirs()
                environments.forEach { env ->
                    buildVariants.forEach { buildVariant ->
                        val bundleId = BASE_APPLICATION_ID +
                            (env.bundleSuffix ?: "") +
                            (buildVariant.applicationIdSuffix ?: "")
                        val appName = BASE_APP_NAME + (env.nameSuffix ?: "") + (buildVariant.nameSuffix ?: "")
                        val compilationConditions = if (env.isMock) "$(inherited) MOCK" else "$(inherited)"
                        // Must match the actual Xcode configuration name (see iosApp/project.yml),
                        // lowercased — CocoaPods names Pods-iosApp.<config>.xcconfig after it,
                        // e.g. "debug-mock", not just the build variant's own "debug"/"release".
                        val xcodeConfigName = if (env.isMock) "${buildVariant.xcodeSuffix}-Mock" else buildVariant.xcodeSuffix
                        val podsConfigName = xcodeConfigName.lowercase()
                        val environmentTag = if (env.isMock) "mock" else "prod"
                        val entitlements = if (buildVariant.isDebug) "iosApp-Debug" else "iosApp-Release"

                        val xcconfig = buildString {
                            appendLine(
                                "// Auto-generated by EnvironmentsConventionPlugin — do not edit manually.",
                            )
                            appendLine("// Source of truth: versions.properties + EnvironmentsConventionPlugin.kt")
                            appendLine()
                            appendLine(
                                "#include? \"../Pods/Target Support Files/Pods-iosApp/" +
                                    "Pods-iosApp.$podsConfigName.xcconfig\"",
                            )
                            appendLine()
                            appendLine("MARKETING_VERSION=$versionName")
                            appendLine("CURRENT_PROJECT_VERSION=$versionCode")
                            appendLine("PRODUCT_BUNDLE_IDENTIFIER=$bundleId")
                            appendLine("PRODUCT_NAME=$appName")
                            appendLine("GASGURU_ENV=$environmentTag")
                            appendLine("CODE_SIGN_ENTITLEMENTS=iosApp/$entitlements.entitlements")
                            appendLine("SWIFT_ACTIVE_COMPILATION_CONDITIONS=$compilationConditions")
                        }

                        val fileName = "gasguru-${env.name.replaceFirstChar { it.uppercase() }}-${buildVariant.xcodeSuffix}.xcconfig"
                        configDir.resolve(fileName).writeText(xcconfig)
                    }
                }
            }
        }

        // Run before any iOS framework embed/sign tasks so Xcode always gets fresh xcconfigs.
        project.tasks.configureEach {
            if (name == "embedAndSignAppleFrameworkForXcode" || name == "syncFramework") {
                dependsOn(generateTask)
            }
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
        const val BASE_APPLICATION_ID = "com.gasguru"
        const val BASE_APP_NAME = "GasGuru"
    }
}
