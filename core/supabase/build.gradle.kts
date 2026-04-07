plugins {
    alias(libs.plugins.gasguru.android.library)
    alias(libs.plugins.gasguru.koin)
    alias(libs.plugins.gasguru.proguard)
    alias(libs.plugins.kotlin.serialization)
    alias(libs.plugins.gasguru.secrets.google)
}

android {
    namespace = "com.gasguru.core.supabase"

    testOptions {
        unitTests {
            isIncludeAndroidResources = true
        }
    }
}

dependencies {
    implementation(platform(libs.supabase.bom))
    implementation(libs.supabase.postgrest)
    implementation(libs.ktor.client.android)
    implementation(libs.kotlinx.coroutines.android)
    implementation(libs.kotlinx.serialization.json)
    implementation(projects.core.analytics)
    implementation(libs.io.arrow.kt.arrow.core)

    testImplementation(projects.core.testing)
    testImplementation(libs.ktor.client.mock)
}