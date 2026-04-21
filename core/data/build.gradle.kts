plugins {
    alias(libs.plugins.gasguru.kmp.library)
    alias(libs.plugins.gasguru.koin)
    alias(libs.plugins.gasguru.proguard)
    alias(libs.plugins.gasguru.secrets.google)
}

android {
    namespace = "com.gasguru.core.data"
}

kotlin {
    sourceSets {
        commonMain.dependencies {
            implementation(projects.core.analytics)
            implementation(projects.core.database)
            implementation(projects.core.model)
            implementation(projects.core.common)
            implementation(projects.core.supabase)
            implementation(projects.core.notifications)
            implementation(libs.io.arrow.kt.arrow.core)
        }
        androidMain.dependencies {
            implementation(projects.core.network)
            implementation(libs.kotlinx.coroutines.android)
            implementation(libs.kotlin.coroutines.play)
            implementation(libs.play.services.location)
            implementation(libs.places)
            implementation(libs.maps.utils)
        }
        commonTest.dependencies {
            implementation(kotlin("test"))
            implementation(libs.kotlinx.coroutines.test)
            implementation(libs.turbine)
        }
    }
}
