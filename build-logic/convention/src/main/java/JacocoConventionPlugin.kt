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
            if (this == rootProject) {
                // Root project: aggregated JaCoCo report for all modules.
                pluginManager.apply("jacoco")
                extensions.configure<org.gradle.testing.jacoco.plugins.JacocoPluginExtension> {
                    toolVersion = "0.8.10"
                }

                tasks.register<JacocoReport>("jacocoRootReport") {
                    group = "verification"
                    description = "Generates aggregated JaCoCo report for all modules."

                    val subprojectsWithTests = rootProject.subprojects.filterNot { project ->
                        project.path in CoverageExclusions.excludedModules
                    }

                    val testTasks = subprojectsWithTests.flatMap { project ->
                        project.tasks.withType<Test>().filter { task ->
                            if (project.path == ":app") {
                                task.name == "testProdDebugUnitTest"
                            } else {
                                true
                            }
                        }
                    }

                    dependsOn(testTasks)

                    val classDirectoriesFiles = subprojectsWithTests.map { project ->
                        project.jacocoClassDirectories()
                    }
                    val sourceDirectoriesFiles = subprojectsWithTests.map { project ->
                        project.jacocoSourceDirectories()
                    }
                    val executionDataFiles = subprojectsWithTests.map { project ->
                        project.jacocoExecutionData()
                    }

                    configureJacocoReport(
                        report = this,
                        classDirectoriesFiles = classDirectoriesFiles,
                        sourceDirectoriesFiles = sourceDirectoriesFiles,
                        executionDataFiles = executionDataFiles
                    )
                }
                return
            }

            // Module project: per-module JaCoCo report.
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
                val testTaskName = if (tasks.names.contains("testProdDebugUnitTest")) {
                    "testProdDebugUnitTest"
                } else {
                    "testDebugUnitTest"
                }
                val execDataFile = layout.buildDirectory.file("jacoco/$testTaskName.exec")

                dependsOn(testTaskName)

                configureJacocoReport(
                    report = this,
                    classDirectoriesFiles = listOf(
                        fileTree(debugClassesDir.get().asFile) { exclude(jacocoExcludes) }
                    ),
                    sourceDirectoriesFiles = listOf(jacocoSourceDirectories()),
                    executionDataFiles = listOf(execDataFile.get().asFile)
                )
            }
        }
    }

    private val jacocoExcludes = CoverageExclusions.excludedFilePatterns

    private fun Project.jacocoClassDirectories() =
        if (path == ":app") {
            fileTree(layout.buildDirectory) {
                include(
                    "tmp/kotlin-classes/prodDebug/**",
                    "intermediates/javac/prodDebug/classes/**"
                )
                exclude(jacocoExcludes)
            }
        } else {
            fileTree(layout.buildDirectory) {
                include(
                    "tmp/kotlin-classes/debug/**",
                    "intermediates/javac/debug/classes/**"
                )
                exclude(jacocoExcludes)
            }
        }

    private fun Project.jacocoSourceDirectories() =
        files(
            "${projectDir}/src/main/java",
            "${projectDir}/src/main/kotlin"
        )

    private fun Project.jacocoExecutionData() =
        if (path == ":app") {
            fileTree(layout.buildDirectory) {
                include("jacoco/testProdDebugUnitTest.exec")
            }
        } else {
            fileTree(layout.buildDirectory) {
                include(
                    "jacoco/testDebugUnitTest.exec",
                    "jacoco/*.exec",
                    "outputs/unit_test_code_coverage/**/*.exec"
                )
            }
        }

    private fun Project.configureJacocoReport(
        report: JacocoReport,
        classDirectoriesFiles: List<Any>,
        sourceDirectoriesFiles: List<Any>,
        executionDataFiles: List<Any>,
    ) {
        report.jacocoClasspath = report.project.configurations.getByName("jacocoAnt")
        report.classDirectories.setFrom(classDirectoriesFiles)
        report.sourceDirectories.setFrom(sourceDirectoriesFiles)
        report.executionData.setFrom(executionDataFiles)

        report.reports {
            xml.required.set(true)
            html.required.set(true)
            xml.outputLocation.set(
                report.project.layout.buildDirectory.file(
                    "reports/jacoco/${report.name}/${report.name}.xml"
                )
            )
            html.outputLocation.set(
                report.project.layout.buildDirectory.dir(
                    "reports/jacoco/${report.name}/html"
                )
            )
        }
    }
}
