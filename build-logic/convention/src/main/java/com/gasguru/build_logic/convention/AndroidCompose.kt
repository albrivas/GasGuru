package com.gasguru.build_logic.convention

import com.android.build.api.dsl.CommonExtension
import org.gradle.api.Project
import org.gradle.api.provider.Provider
import org.gradle.kotlin.dsl.configure
import org.gradle.kotlin.dsl.dependencies
import org.jetbrains.kotlin.compose.compiler.gradle.ComposeCompilerGradlePluginExtension

internal fun Project.configureAndroidCompose(
    commonExtension: CommonExtension<*, *, *, *, *, *>,
) {
    commonExtension.apply {
        buildFeatures {
            compose = true
        }
    }

    dependencies {
        add("implementation", getLibrary("androidx.activity.compose"))
        add("implementation", getLibrary("androidx.lifecycle.runtime.ktx"))
        add("implementation", getLibrary("androidx.lifecycle.viewmodel.compose"))
        add("implementation", getLibrary("androidx.hilt.navigation.compose"))
        add("implementation", getLibrary("androidx.compose.ui"))
        add("implementation", getLibrary("androidx.compose.ui.tooling.preview"))
        add("implementation", getLibrary("androidx.compose.material3"))
        add("implementation", getLibrary("androidx.compose.permission"))
        add("implementation", getLibrary("androidx.navigation.compose"))
        add("implementation", getLibrary("material.icons.extended"))
        add("implementation", getLibrary("coil.compose"))
        add("implementation", getLibrary("kotlinx.serialization.json"))
        add("debugImplementation", getLibrary("androidx.compose.ui.tooling"))
        add("debugImplementation", getLibrary("androidx.compose.ui.test.manifest"))
        add("androidTestImplementation", getLibrary("junit5.compose"))
    }

    @Suppress("UnstableApiUsage")
    extensions.configure<ComposeCompilerGradlePluginExtension> {
        fun Provider<String>.onlyIfTrue() = flatMap { provider { it.takeIf(String::toBoolean) } }
        fun Provider<*>.relativeToRootProject(dir: String) = map {
            isolated.rootProject.projectDirectory
                .dir("build")
                .dir(projectDir.toRelativeString(rootDir))
        }.map { it.dir(dir) }

        project.providers.gradleProperty("enableComposeCompilerMetrics").onlyIfTrue()
            .relativeToRootProject("compose-metrics")
            .let(metricsDestination::set)

        project.providers.gradleProperty("enableComposeCompilerReports").onlyIfTrue()
            .relativeToRootProject("compose-reports")
            .let(reportsDestination::set)

        stabilityConfigurationFiles
            .add(isolated.rootProject.projectDirectory.file("compose_compiler_config.conf"))
    }
}
