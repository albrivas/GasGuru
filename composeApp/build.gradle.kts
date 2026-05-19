plugins {
    alias(libs.plugins.gasguru.kmp.compose.library)
    alias(libs.plugins.gasguru.koin)
    kotlin("native.cocoapods")
}

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

        framework {
            baseName = "ComposeApp"
            isStatic = true
        }
    }

    sourceSets {
        commonMain.dependencies {
            implementation(projects.core.uikit)
            implementation(projects.core.ui)
            implementation(projects.core.analytics)
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
            implementation(kotlin("test"))
            implementation(libs.kotlinx.coroutines.test)
            implementation(libs.turbine)
            implementation(projects.core.testing)
            implementation(projects.core.database)
        }
        androidUnitTest.dependencies {
            implementation(libs.junit5.api)
            implementation(libs.junit5.extensions)
            implementation(libs.junit5.engine)
        }
    }
}
