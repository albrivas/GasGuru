
import com.android.build.gradle.LibraryExtension
import com.gasguru.build_logic.convention.getLibrary
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.configure
import org.jetbrains.compose.ComposePlugin
import org.jetbrains.kotlin.gradle.dsl.KotlinMultiplatformExtension

class KmpComposeLibraryConventionPlugin : Plugin<Project> {
    override fun apply(target: Project) {
        with(target) {
            pluginManager.apply("gasguru.kmp.library")
            pluginManager.apply("org.jetbrains.compose")
            pluginManager.apply("org.jetbrains.kotlin.plugin.compose")
            pluginManager.apply("org.jetbrains.kotlin.plugin.serialization")

            extensions.configure<KotlinMultiplatformExtension> {
                val compose = ComposePlugin.Dependencies(this@with)

                sourceSets.commonMain.dependencies {
                    implementation(compose.runtime)
                    implementation(compose.foundation)
                    implementation(compose.material3)
                    implementation(compose.ui)
                    api(compose.components.resources)
                    implementation(getLibrary("kotlinx.serialization.json"))
                }
                sourceSets.androidMain.dependencies {
                    implementation(getLibrary("koin.androidx.compose"))
                    implementation(getLibrary("androidx.lifecycle.viewmodel.compose"))
                    implementation(getLibrary("androidx.navigation.compose"))
                    implementation(getLibrary("coil.compose"))
                }
            }
        }
    }
}