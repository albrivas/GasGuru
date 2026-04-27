plugins {
    alias(libs.plugins.gasguru.kmp.compose.library)
}

android {
    namespace = "com.gasguru.core.uikit"
}

dependencies {
    debugImplementation(compose.uiTooling)
}

compose.resources {
    publicResClass = true
    packageOfResClass = "com.gasguru.core.uikit.generated.resources"
}

kotlin {
    sourceSets {
        commonMain.dependencies {
            implementation(compose.materialIconsExtended)
            implementation(compose.components.uiToolingPreview)
        }
        androidMain.dependencies {
            implementation(libs.androidx.core.ktx)
        }
        androidInstrumentedTest.dependencies {
            implementation(projects.core.testing)
            implementation(libs.junit5.compose)
            implementation(libs.junit5.api)
            implementation(libs.junit5.extensions)
        }
    }
}
