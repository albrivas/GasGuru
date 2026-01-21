
import com.android.build.gradle.LibraryExtension
import com.gasguru.build_logic.convention.configureDetekt
import com.gasguru.build_logic.convention.configureKotlinAndroid
import com.gasguru.build_logic.convention.getLibrary
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.tasks.testing.Test
import org.gradle.kotlin.dsl.apply
import org.gradle.kotlin.dsl.configure
import org.gradle.kotlin.dsl.dependencies
import org.gradle.kotlin.dsl.withType

class AndroidLibraryConventionPlugin : Plugin<Project> {
    override fun apply(target: Project) {
        with(target) {
            apply(plugin = "com.android.library")
            apply(plugin = "org.jetbrains.kotlin.android")
            apply(plugin = "gasguru.jacoco")

            tasks.withType<Test>().configureEach {
                useJUnitPlatform()
            }

            extensions.configure<LibraryExtension> {
                configureKotlinAndroid(this)
                configureDetekt(this)

                defaultConfig.apply {
                    targetSdk = 36
                    testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
                    testInstrumentationRunnerArguments["runnerBuilder"] =
                        "de.mannodermaus.junit5.AndroidJUnit5Builder"
                }

                testOptions.animationsDisabled = true

                dependencies {
                    add("androidTestRuntimeOnly", getLibrary("junit5.runner"))
                    add("testImplementation", getLibrary("junit5.api"))
                    add("testRuntimeOnly", getLibrary("junit5.engine"))
                }
            }
        }
    }
}
