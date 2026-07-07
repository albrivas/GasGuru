plugins {
    alias(libs.plugins.gasguru.kmp.compose.library)
    alias(libs.plugins.gasguru.koin)
    alias(libs.plugins.gasguru.flavors)
    kotlin("native.cocoapods")
}

// Detect mock vs prod from Xcode's CONFIGURATION env var (e.g. "Debug-Mock" / "Release-Mock").
// This is set by Xcode when it invokes Gradle to embed the KMP framework.
val isMockIosBuild = System.getenv("CONFIGURATION")?.contains("Mock", ignoreCase = true) == true

android {
    namespace = "com.gasguru.composeApp"
}

compose.resources {
    publicResClass = true
    packageOfResClass = "com.gasguru.composeApp.generated.resources"
}

kotlin {
    cocoapods {
        summary = "GasGuru shared Compose Multiplatform layer"
        homepage = "https://github.com/gasguru/GasGuru"
        version = "1.0"
        ios.deploymentTarget = "15.0"

        // Map custom Xcode configurations to native build types so that
        // the Kotlin/Native cocoapods plugin can resolve them correctly.
        // Without this, "Debug-Mock" / "Release-Mock" configs break syncFramework.
        xcodeConfigurationToNativeBuildType["Debug-Mock"] =
            org.jetbrains.kotlin.gradle.plugin.mpp.NativeBuildType.DEBUG
        xcodeConfigurationToNativeBuildType["Release-Mock"] =
            org.jetbrains.kotlin.gradle.plugin.mpp.NativeBuildType.RELEASE

        framework {
            baseName = "ComposeApp"
            isStatic = true
            export(projects.core.analytics)
            export(projects.core.notifications)
        }
    }

    sourceSets {
        commonMain.dependencies {
            implementation(projects.core.uikit)
            implementation(projects.core.ui)
            api(projects.core.analytics)
            implementation(projects.core.common)
            implementation(projects.core.data)
            implementation(projects.core.domain)
            implementation(projects.core.model)
            implementation(projects.navigation)
            implementation(projects.feature.detailStation)
            implementation(projects.feature.favoriteListStation)
            implementation(projects.feature.onboarding)
            implementation(projects.feature.profile)
            implementation(projects.feature.routePlanner)
            implementation(projects.feature.search)
            implementation(projects.feature.stationMap)
            implementation(projects.feature.vehicle)
        }
        commonTest.dependencies {
            implementation(libs.kotlin.test)
            implementation(libs.kotlinx.coroutines.test)
            implementation(libs.turbine)
            implementation(projects.core.testing)
            implementation(projects.core.database)
        }
        androidMain.dependencies {
            implementation(libs.koin.android)
            implementation(projects.core.analytics)
            implementation(projects.core.database)
            implementation(projects.core.network)
            implementation(projects.core.notifications)
            implementation(projects.core.supabase)
            implementation(projects.core.components)
        }
        iosMain.dependencies {
            implementation(projects.core.database)
            implementation(projects.core.supabase)
            api(projects.core.notifications)
            implementation(projects.core.components)
            // :mocknetwork (with 12MB JSON) is only linked when building for Mock scheme.
            // iOS-prod stays clean — parity with Android's mockImplementation.
            if (isMockIosBuild) {
                implementation(projects.mocknetwork)
            }
        }
        androidUnitTest.dependencies {
            implementation(libs.junit5.api)
            implementation(libs.junit5.extensions)
            implementation(libs.junit5.engine)
        }
    }
}
