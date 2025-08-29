import com.android.build.gradle.LibraryExtension
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.configure

class ProguardConventionPlugin : Plugin<Project> {
    override fun apply(target: Project) {
        with(target) {
            pluginManager.withPlugin("com.android.library") {
                extensions.configure<LibraryExtension> {
                    val proguardFile = file("proguard-rules.pro")
                    if (proguardFile.exists()) {
                        defaultConfig {
                            consumerProguardFiles("proguard-rules.pro")
                        }
                    }
                }
            }
        }
    }
}