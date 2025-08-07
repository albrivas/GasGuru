
import com.android.build.api.dsl.ApplicationExtension
import com.android.build.api.dsl.LibraryExtension
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.apply
import org.gradle.kotlin.dsl.configure

class SecretsConventionPlugin : Plugin<Project> {
    override fun apply(target: Project) {
        with(target) {
            apply(plugin = "com.google.android.libraries.mapsplatform.secrets-gradle-plugin")

            // Necessary because apply in module app (application type)
            pluginManager.withPlugin("com.android.application") {
                configure<ApplicationExtension> {
                    buildFeatures {
                        buildConfig = true
                    }
                }
            }

            pluginManager.withPlugin("com.android.library") {
                configure<LibraryExtension> {
                    buildFeatures {
                        buildConfig = true
                    }
                }
            }

            afterEvaluate {
                extensions.configure<com.google.android.libraries.mapsplatform.secrets_gradle_plugin.SecretsPluginExtension> {

                    propertiesFileName = "local.properties"

                    ignoreList.add("storePassword")
                    ignoreList.add("sdk.*")
                    ignoreList.add("keyAlias")
                    ignoreList.add("keyPassword")
                    ignoreList.add("sha1Debug")
                    ignoreList.add("sha1PlayStore")
                }
            }
        }
    }
}