plugins {
    alias(libs.plugins.gasguru.kmp.library)
    alias(libs.plugins.gasguru.koin)
    alias(libs.plugins.gasguru.proguard)
    kotlin("native.cocoapods")
}

android {
    namespace = "com.gasguru.core.analytics"

    testOptions {
        unitTests.isReturnDefaultValues = true
    }

    buildFeatures {
        buildConfig = true
    }
}

kotlin {
    cocoapods {
        summary = "GasGuru Analytics — Mixpanel integration for iOS"
        homepage = "https://github.com/gasguru/GasGuru"
        version = "1.0"
        ios.deploymentTarget = "15.0"

        pod("Mixpanel-swift") {
            version = "~> 4.2"
        }
    }

    sourceSets {
        commonMain.dependencies {
            // AnalyticsEvent, AnalyticsHelper, NoOpAnalyticsHelper — Kotlin puro
        }
        androidMain.dependencies {
            implementation(libs.androidx.compose.runtime)
            implementation(libs.mixpanel)
        }
        commonTest.dependencies {
            implementation(kotlin("test"))
        }
    }
}

dependencies {
    // Tests Android-específicos: LogcatAnalyticsHelperTest, MixpanelAnalyticsHelperTest
    testImplementation(libs.mockk)
    testImplementation(libs.json)
    testImplementation(libs.junit5.api)
    testRuntimeOnly(libs.junit5.engine)
}
