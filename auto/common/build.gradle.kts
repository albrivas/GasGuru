plugins {
    alias(libs.plugins.gasguru.android.library)
    alias(libs.plugins.gasguru.compose.library)
    alias(libs.plugins.gasguru.hilt)
    alias(libs.plugins.secrets)
}

android {
    namespace = "com.gasguru.auto.common"
}

dependencies {
    implementation(projects.core.domain)
    implementation(projects.core.model)
    implementation(projects.core.common)
    implementation(projects.core.uikit)
    implementation(projects.core.ui)
    implementation(libs.androidx.auto)
    implementation(libs.androidx.car.app)
    implementation(libs.kotlinx.serialization.json)
}