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
                        "sonar.coverage.jacoco.xmlReportPaths",
                        "${rootProject.layout.buildDirectory.get()}/reports/jacoco/jacocoRootReport/jacocoRootReport.xml",
                    )
                    property("sonar.exclusions",
                        CoverageExclusions.sonarCoverageExclusions,
                    )
                    property(
                        "sonar.coverage.exclusions",
                        CoverageExclusions.sonarCoverageExclusions,
                    )
                    // SonarScanner 6.x tries to auto-download a JRE from SonarCloud
                    // (/analysis/jres endpoint). That call requires extra token permissions
                    // and fails with 403. The GitHub Actions runner already has Java 21,
                    // so we skip JRE provisioning entirely.
                    property("sonar.scanner.skipJreProvisioning", "true")
                }
            }
        }
    }
}
