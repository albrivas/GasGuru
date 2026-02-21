plugins {
    alias(libs.plugins.gasguru.android.library)
    alias(libs.plugins.gasguru.koin)
    alias(libs.plugins.ksp)
    alias(libs.plugins.kotlin.serialization)
    alias(libs.plugins.gasguru.secrets.google)
    alias(libs.plugins.gasguru.proguard)
}

android {
    namespace = "com.gasguru.core.network"

    testOptions {
        unitTests {
            isIncludeAndroidResources = true
        }
    }
}

dependencies {
    implementation(projects.core.common)
    implementation(libs.bundles.moshi)
    ksp(libs.moshi.codegen)
    implementation(libs.bundles.com.squareup.retrofit2)
    implementation(libs.kotlinx.coroutines.android)
    implementation(libs.kotlinx.serialization.json)
    implementation(libs.io.arrow.kt.arrow.core)
    implementation(libs.kotlin.coroutines.play)
    implementation(libs.places)

    testImplementation(libs.mock.webserver)
    testImplementation(projects.core.testing)
}
