
import com.android.build.gradle.BaseExtension
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.configure

class FlavorsConventionPlugin : Plugin<Project> {
    override fun apply(project: Project) {
        project.plugins.withId("com.android.application") {
            project.extensions.configure<BaseExtension> {
                flavorDimensions("environment")
                productFlavors {
                    create("mock") {
                        dimension = "environment"
                        applicationIdSuffix = ".mock"
                        versionNameSuffix = "-mock"
                    }
                    create("prod") {
                        dimension = "environment"
                    }
                }
            }
        }
    }
}