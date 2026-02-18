plugins {
    alias(libs.plugins.gasguru.android.library)
    alias(libs.plugins.gasguru.hilt)
    alias(libs.plugins.gasguru.proguard)
    alias(libs.plugins.gasguru.secrets.google)
}

android {
    namespace = "com.gasguru.mocknetwork"
}

dependencies {
    implementation(projects.core.network)
    implementation(projects.core.common)
    implementation(libs.ktor.client.mock)
    implementation(libs.ktor.client.content.negotiation)
    implementation(libs.ktor.serialization.kotlinx.json)
    implementation(libs.io.arrow.kt.arrow.core)
}
