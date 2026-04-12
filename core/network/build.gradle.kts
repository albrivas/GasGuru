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
    implementation(projects.core.analytics)
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

// ktor-client-okhttp (transitivo via core:testing → core:supabase) fuerza OkHttp 5.x,
// incompatible con mockwebserver 4.x (okhttp3.internal.Util eliminado en 5.x).
configurations.matching { it.name.contains("UnitTest", ignoreCase = true) || it.name.startsWith("test") }.configureEach {
    resolutionStrategy {
        force("com.squareup.okhttp3:okhttp:4.12.0")
    }
}
