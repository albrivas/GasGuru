@file:OptIn(org.jetbrains.compose.ExperimentalComposeLibrary::class)

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
        jvmTest.dependencies {
            implementation(compose.desktop.currentOs)
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

// GasGuruSearchBarContentTest uses runComposeUiTest with the Skiko renderer.
// jvmTest has compose.desktop.currentOs (Skiko desktop, runs headless).
// testDebugUnitTest (androidUnitTest) resolves compose.ui.test to the Android artefact
// which requires instrumentation — exclude it there to avoid NullPointerExceptions.
tasks.withType<Test>().configureEach {
    if (name != "jvmTest") {
        exclude("**/GasGuruSearchBarContentTest*")
    }
}
