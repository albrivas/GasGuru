plugins {
    alias(libs.plugins.gasguru.android.library)
    alias(libs.plugins.gasguru.hilt)
    alias(libs.plugins.gasguru.compose.library)
    alias(libs.plugins.gasguru.proguard)
    alias(libs.plugins.stability.analyzer)
}

android {
    namespace = "com.gasguru.feature.favorite_list_station"
}

dependencies {
    implementation(projects.core.ui)
    implementation(projects.core.domain)
    implementation(projects.core.model)
    implementation(projects.core.uikit)
    implementation(projects.core.common)
    implementation(projects.navigation)

    androidTestImplementation(projects.core.testing)
    androidTestImplementation(projects.core.ui)
    testImplementation(projects.core.testing)
    androidTestImplementation(libs.junit5.api)
    androidTestImplementation(libs.junit5.extensions)
}
