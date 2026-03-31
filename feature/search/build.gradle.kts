plugins {
    alias(libs.plugins.gasguru.android.library)
    alias(libs.plugins.gasguru.koin)
    alias(libs.plugins.gasguru.compose.library)
    alias(libs.plugins.gasguru.secrets.google)
    alias(libs.plugins.gasguru.proguard)
    alias(libs.plugins.stability.analyzer)
}

android {
    namespace = "com.gasguru.feature.search"
}

dependencies {
    implementation(projects.core.ui)
    implementation(projects.core.domain)
    implementation(projects.core.model)
    implementation(projects.core.uikit)
    implementation(projects.core.common)
    implementation(projects.core.components)
    implementation(projects.navigation)
    
    implementation(libs.kotlin.coroutines.play)
    implementation(libs.maps.compose)
    implementation(libs.play.services.maps)

    androidTestImplementation(projects.core.testing)
    androidTestImplementation(projects.core.testing)
    androidTestImplementation(libs.junit5.api)
    androidTestImplementation(libs.junit5.extensions)
}