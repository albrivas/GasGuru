plugins {
    alias(libs.plugins.gasguru.android.library)
    alias(libs.plugins.gasguru.hilt)
    alias(libs.plugins.gasguru.proguard)
}

android {
    namespace = "com.gasguru.core.data"
}

dependencies {
    implementation(projects.core.database)
    implementation(projects.core.network)
    implementation(projects.core.model)
    implementation(projects.core.common)
    implementation(libs.kotlinx.coroutines.android)
    implementation(libs.kotlin.coroutines.play)
    implementation(libs.io.arrow.kt.arrow.core)
    implementation(libs.play.services.location)
    implementation(libs.places)

    testImplementation(projects.core.testing)
}
