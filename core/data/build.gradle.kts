plugins {
    alias(libs.plugins.gasguru.android.library)
    alias(libs.plugins.gasguru.koin)
    alias(libs.plugins.gasguru.proguard)
    alias(libs.plugins.gasguru.secrets.google)
}

android {
    namespace = "com.gasguru.core.data"
}

dependencies {
    implementation(projects.core.database)
    implementation(projects.core.network)
    implementation(projects.core.model)
    implementation(projects.core.common)
    implementation(projects.core.supabase)
    implementation(projects.core.notifications)
    implementation(libs.kotlinx.coroutines.android)
    implementation(libs.kotlin.coroutines.play)
    implementation(libs.io.arrow.kt.arrow.core)
    implementation(libs.play.services.location)
    implementation(libs.places)
    implementation(libs.maps.utils)

    testImplementation(projects.core.testing)
}
