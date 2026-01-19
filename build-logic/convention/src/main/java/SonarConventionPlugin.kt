import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.configure
import org.sonarqube.gradle.SonarExtension

class SonarConventionPlugin : Plugin<Project> {
    override fun apply(target: Project) {
        with(target) {
            if (this != rootProject) return

            pluginManager.apply("org.sonarqube")

            extensions.configure<SonarExtension> {
                properties {
                    property("sonar.projectKey", "albrivas_FuelPump")
                    property("sonar.organization", "fuel-pump")
                    property("sonar.language", "kotlin")
                    property("sonar.sourceEncoding", "UTF-8")
                    property(
                        "sonar.projectVersion",
                        System.getenv("GITHUB_SHA")
                            ?: System.getenv("GITHUB_RUN_NUMBER")
                            ?: "local",
                    )
                    property(
                        "sonar.coverage.jacoco.xmlReportPaths",
                        "${rootProject.layout.buildDirectory.get()}/reports/jacoco/jacocoRootReport/jacocoRootReport.xml",
                    )
                }
            }
        }
    }
}
