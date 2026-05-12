plugins {
    alias(libs.plugins.gasguru.kmp.compose.library)
    kotlin("native.cocoapods")
}

android {
    namespace = "com.gasguru.composeApp"
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
        }
    }
}
