
import com.android.build.gradle.LibraryExtension
import com.gasguru.build_logic.convention.configureDetekt
import com.gasguru.build_logic.convention.configureKotlinAndroid
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.tasks.testing.Test
import org.gradle.kotlin.dsl.configure
import org.gradle.kotlin.dsl.withType
import org.jetbrains.kotlin.gradle.dsl.KotlinMultiplatformExtension

class KmpLibraryConventionPlugin : Plugin<Project> {
    override fun apply(target: Project) {
        with(target) {
            with(pluginManager) {
                apply("org.jetbrains.kotlin.multiplatform")
                apply("com.android.library")
                apply("gasguru.jacoco")
            }
            configureDetekt()
            extensions.configure<LibraryExtension> {
                configureKotlinAndroid(this)
            }
            extensions.configure<KotlinMultiplatformExtension> {
                androidTarget()
                iosX64()
                iosArm64()
                iosSimulatorArm64()
            }
            tasks.withType<Test>().configureEach {
                useJUnitPlatform()
            }
            // kotlin("test") in commonTest resolves to kotlin-test-junit (JUnit4) on Android
            // instrumented test classpaths when JUnit4 is already present (e.g. via
            // androidx.compose.ui.test.junit4). core:testing exposes kotlin-test-junit5 via
            // androidMain API. Both provide the kotlin-test-framework-impl capability → conflict.
            // Resolve by always preferring the JUnit5 variant, which is what the project uses.
            configurations.all {
                resolutionStrategy.capabilitiesResolution.withCapability(
                    "org.jetbrains.kotlin:kotlin-test-framework-impl",
                ) {
                    val junit5Candidate = candidates.firstOrNull { candidate ->
                        candidate.id.toString().contains("junit5")
                    }
                    if (junit5Candidate != null) {
                        select(junit5Candidate)
                    } else {
                        selectHighestVersion()
                    }
                }
            }
        }
    }
}