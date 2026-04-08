
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
        }
    }
}