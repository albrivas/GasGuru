plugins {
    alias(libs.plugins.gasguru.kmp.compose.library)
    alias(libs.plugins.gasguru.koin)
}

android {
    namespace = "com.gasguru.mocknetwork"
}

compose.resources {
    publicResClass = false
    packageOfResClass = "com.gasguru.mocknetwork.generated.resources"
}

kotlin {
    sourceSets {
        commonMain.dependencies {
            implementation(projects.core.common)
            implementation(projects.core.supabase)
            implementation(libs.kotlinx.serialization.json)
            implementation(libs.io.arrow.kt.arrow.core)
        }
    }
}
