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
    packageOfResClass = "com.gasguru.feature.favorite_list_station.generated.resources"
}

kotlin {
    sourceSets {
        commonMain.dependencies {
            implementation(projects.core.analytics)
            implementation(projects.core.ui)
            implementation(projects.core.domain)
            implementation(projects.core.model)
            implementation(projects.core.uikit)
            implementation(projects.core.common)
            implementation(projects.navigation)
            implementation(libs.jetbrains.compose.ui.tooling.preview)
        }
        commonTest.dependencies {
            implementation(kotlin("test"))
            implementation(libs.kotlinx.coroutines.test)
            implementation(libs.turbine)
            implementation(projects.core.testing)
        }
        androidUnitTest.dependencies {
            implementation(libs.junit5.api)
            implementation(libs.junit5.extensions)
            implementation(libs.junit5.engine)
        }
    }
}

android {
    namespace = "com.gasguru.feature.favorite_list_station"
    defaultConfig {
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }
}

dependencies {
    androidTestImplementation(libs.androidx.compose.ui.test.junit4)
    androidTestImplementation(projects.core.testing)
    debugImplementation(libs.androidx.compose.ui.test.manifest)
    debugImplementation(compose.uiTooling)
    androidTestImplementation(libs.junit5.api)
    androidTestImplementation(libs.junit5.extensions)
}
