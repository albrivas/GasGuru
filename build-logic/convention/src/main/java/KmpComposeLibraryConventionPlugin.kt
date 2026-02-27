
import com.android.build.gradle.LibraryExtension
import com.gasguru.build_logic.convention.getLibrary
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.configure
import org.jetbrains.kotlin.gradle.dsl.KotlinMultiplatformExtension

class KmpComposeLibraryConventionPlugin : Plugin<Project> {
    override fun apply(target: Project) {
        with(target) {
            pluginManager.apply("gasguru.kmp.library")
            pluginManager.apply("org.jetbrains.compose")
            pluginManager.apply("org.jetbrains.kotlin.plugin.compose")
            pluginManager.apply("org.jetbrains.kotlin.plugin.serialization")

            extensions.configure<LibraryExtension> {
                buildFeatures.compose = true
            }

            extensions.configure<KotlinMultiplatformExtension> {
                sourceSets.commonMain.dependencies {
                    implementation(getLibrary("compose.multiplatform.runtime"))
                    implementation(getLibrary("compose.multiplatform.foundation"))
                    implementation(getLibrary("compose.multiplatform.material3"))
                    implementation(getLibrary("compose.multiplatform.ui"))
                    implementation(getLibrary("compose.multiplatform.components.resources"))
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