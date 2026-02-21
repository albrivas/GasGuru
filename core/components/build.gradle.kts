plugins {
    alias(libs.plugins.gasguru.android.library)
    alias(libs.plugins.gasguru.koin)
    alias(libs.plugins.gasguru.compose.library)
    alias(libs.plugins.gasguru.proguard)
}

android {
    namespace = "com.gasguru.core.components"
}

dependencies {
    implementation(projects.core.ui)
    implementation(projects.core.domain)
    implementation(projects.core.model)
    implementation(projects.core.uikit)
    implementation(projects.core.common)
    implementation(libs.koin.androidx.compose)

    androidTestImplementation(projects.core.testing)
    testImplementation(projects.core.testing)
    androidTestImplementation(libs.junit5.api)
    androidTestImplementation(libs.junit5.extensions)
}