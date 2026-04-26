plugins {
    alias(libs.plugins.gasguru.android.library)
    alias(libs.plugins.gasguru.koin)
    alias(libs.plugins.kotlin.serialization)
    alias(libs.plugins.gasguru.proguard)
}

android {
    namespace = "com.gasguru.mocknetwork"
}

dependencies {
    implementation(projects.core.supabase)
    implementation(projects.core.common)
    implementation(libs.kotlinx.serialization.json)
    implementation(libs.io.arrow.kt.arrow.core)
}
