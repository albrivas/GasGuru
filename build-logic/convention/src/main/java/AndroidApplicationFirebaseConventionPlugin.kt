import com.android.build.api.dsl.ApplicationExtension
import com.gasguru.build_logic.convention.getLibrary
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.apply
import org.gradle.kotlin.dsl.configure
import org.gradle.kotlin.dsl.dependencies

class AndroidApplicationFirebaseConventionPlugin : Plugin<Project> {
    override fun apply(target: Project) {
        with(target) {
            apply(plugin = "com.google.firebase.crashlytics")

            // Configure Crashlytics mapping upload for release builds
            pluginManager.withPlugin("com.android.application") {
                extensions.configure<ApplicationExtension> {
                    buildTypes {
                        getByName("release") {
                            configure<com.google.firebase.crashlytics.buildtools.gradle.CrashlyticsExtension> {
                                mappingFileUploadEnabled = true
                            }
                        }
                    }
                }
            }

            dependencies {
                add("implementation", getLibrary("firebase.crashlytics"))
            }
        }
    }
}
