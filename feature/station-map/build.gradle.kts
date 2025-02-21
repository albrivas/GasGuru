plugins {
    alias(libs.plugins.gasguru.android.library)
    alias(libs.plugins.gasguru.hilt)
    alias(libs.plugins.gasguru.compose.library)
    alias(libs.plugins.secrets)
}

android {
    namespace = "com.gasguru.feature.station_map"

    buildFeatures {
        buildConfig = true
    }
}

dependencies {
    implementation(projects.core.ui)
    implementation(projects.core.domain)
    implementation(projects.core.model)
    implementation(projects.core.uikit)
    implementation(projects.core.common)
    
    implementation(libs.kotlin.coroutines.play)
    implementation(libs.maps.compose)
    implementation(libs.play.services.maps)
    implementation(libs.places)

    androidTestImplementation(projects.core.testing)
    androidTestImplementation(projects.core.testing)
    androidTestImplementation(libs.junit5.api)
    androidTestImplementation(libs.junit5.extensions)
    androidTestRuntimeOnly(libs.junit5.runner)
}