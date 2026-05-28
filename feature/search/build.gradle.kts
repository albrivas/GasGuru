@file:OptIn(
    org.jetbrains.compose.ExperimentalComposeLibrary::class,
    org.jetbrains.kotlin.gradle.ExperimentalKotlinGradlePluginApi::class,
)

plugins {
    alias(libs.plugins.gasguru.kmp.compose.library)
    alias(libs.plugins.gasguru.koin)
    alias(libs.plugins.gasguru.proguard)
    alias(libs.plugins.stability.analyzer)
}

compose.resources {
    publicResClass = true
    packageOfResClass = "com.gasguru.feature.search.generated.resources"
}

kotlin {
    sourceSets {
        commonMain.dependencies {
            implementation(projects.core.ui)
            implementation(projects.core.uikit)
            implementation(projects.core.model)
            implementation(projects.core.components)
            implementation(projects.navigation)
            implementation(libs.jetbrains.compose.ui.tooling.preview)
        }
    }
}

android {
    namespace = "com.gasguru.feature.search"
    defaultConfig {
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }
}

dependencies {
    debugImplementation(compose.uiTooling)
}
