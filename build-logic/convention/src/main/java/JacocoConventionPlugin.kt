import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.tasks.testing.Test
import org.gradle.kotlin.dsl.configure
import org.gradle.kotlin.dsl.register
import org.gradle.kotlin.dsl.withType
import org.gradle.testing.jacoco.tasks.JacocoReport

class JacocoConventionPlugin : Plugin<Project> {
    override fun apply(target: Project) {
        with(target) {
            pluginManager.apply("jacoco")

            extensions.configure<org.gradle.testing.jacoco.plugins.JacocoPluginExtension> {
                toolVersion = "0.8.10"
            }

            tasks.withType<Test>().configureEach {
                useJUnitPlatform()
                finalizedBy("jacocoTestReport")
            }

            tasks.register<JacocoReport>("jacocoTestReport") {
                group = "verification"
                description = "Generates JaCoCo coverage report."

                val debugClassesDir = layout.buildDirectory.dir("intermediates/classes/debug")
                val execDataFile = layout.buildDirectory.file("jacoco/testDebugUnitTest.exec")

                dependsOn("testDebugUnitTest")

                reports {
                    xml.required.set(true)
                    html.required.set(true)
                }

                classDirectories.setFrom(
                    fileTree(debugClassesDir.get().asFile) {
                        exclude(
                            "**/di/**",
                            "**/BuildConfig.*",
                            "**/R.class",
                            "**/R\$*.class"
                        )
                    }
                )

                sourceDirectories.setFrom(
                    files("src/main/java", "src/main/kotlin")
                )

                executionData.setFrom(execDataFile.get().asFile)
            }
        }
    }
}