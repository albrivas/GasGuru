@file:OptIn(
    org.jetbrains.compose.ExperimentalComposeLibrary::class,
    org.jetbrains.kotlin.gradle.ExperimentalKotlinGradlePluginApi::class,
)

plugins {
    alias(libs.plugins.gasguru.kmp.compose.library)
    alias(libs.plugins.gasguru.koin)
    alias(libs.plugins.gasguru.proguard)
}

compose.resources {
    publicResClass = true
    packageOfResClass = "com.gasguru.core.components.generated.resources"
}

kotlin {
    androidTarget {
        // Connect commonTest to the Android instrumented test variant
        // so that `connectedAndroidTest` picks up CMP UI tests from commonTest
        instrumentedTestVariant.sourceSetTree.set(org.jetbrains.kotlin.gradle.plugin.KotlinSourceSetTree.test)
    }

    sourceSets {
        commonMain.dependencies {
            implementation(projects.core.analytics)
            implementation(projects.core.ui)
            implementation(projects.core.domain)
            implementation(projects.core.model)
            implementation(projects.core.uikit)
            implementation(projects.core.common)
        }
        commonTest.dependencies {
            implementation(libs.kotlin.test)
            implementation(libs.kotlinx.coroutines.test)
            implementation(libs.turbine)
            implementation(projects.core.testing)
            implementation(compose.uiTest)
        }
        androidUnitTest.dependencies {
            implementation(libs.junit5.api)
            implementation(libs.junit5.engine)
            implementation(libs.junit5.extensions)
        }
    }
}

android {
    namespace = "com.gasguru.core.components"
    defaultConfig {
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }
}

dependencies {
    androidTestImplementation(libs.androidx.compose.ui.test.junit4)
    debugImplementation(libs.androidx.compose.ui.test.manifest)
}

// GasGuruSearchBarContentTest uses runComposeUiTest (Skiko renderer) — needs a display.
// Runs via connectedAndroidTest only; exclude from all JVM-based test tasks.
tasks.withType<Test>().configureEach {
    exclude("**/GasGuruSearchBarContentTest*")
}
