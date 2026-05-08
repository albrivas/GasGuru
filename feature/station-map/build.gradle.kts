@file:OptIn(
    org.jetbrains.compose.ExperimentalComposeLibrary::class,
    org.jetbrains.kotlin.gradle.ExperimentalKotlinGradlePluginApi::class,
)

plugins {
    alias(libs.plugins.gasguru.kmp.compose.library)
    alias(libs.plugins.gasguru.koin)
    alias(libs.plugins.gasguru.secrets.google)
    alias(libs.plugins.gasguru.proguard)
    alias(libs.plugins.stability.analyzer)
}

compose.resources {
    publicResClass = true
    packageOfResClass = "com.gasguru.feature.station_map.generated.resources"
}

kotlin {
    androidTarget {
        instrumentedTestVariant.sourceSetTree.set(
            org.jetbrains.kotlin.gradle.plugin.KotlinSourceSetTree.test
        )
    }

    sourceSets {
        commonMain.dependencies {
            implementation(projects.core.analytics)
            implementation(projects.core.ui)
            implementation(projects.core.domain)
            implementation(projects.core.model)
            implementation(projects.core.uikit)
            implementation(projects.core.common)
            implementation(projects.core.components)
            implementation(projects.navigation)
            implementation(compose.components.uiToolingPreview)
        }
        androidMain.dependencies {
            implementation(libs.maps.compose)
            implementation(libs.play.services.maps)
            implementation(libs.maps.utils)
        }
        commonTest.dependencies {
            implementation(kotlin("test"))
            implementation(libs.kotlinx.coroutines.test)
            implementation(libs.turbine)
            implementation(projects.core.testing)
            implementation(libs.junit5.api)
            implementation(libs.junit5.extensions)
        }
        androidUnitTest.dependencies {
            implementation(libs.junit5.engine)
        }
    }
}

android {
    namespace = "com.gasguru.feature.station_map"
    defaultConfig {
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }
}

dependencies {
    debugImplementation(compose.uiTooling)
}
