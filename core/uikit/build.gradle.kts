plugins {
    alias(libs.plugins.gasguru.android.library)
    alias(libs.plugins.gasguru.compose.library)
}

android {
    namespace = "com.gasguru.core.uikit"
}

dependencies {
    implementation(projects.core.testing)
    implementation(libs.androidx.core.ktx)
    implementation(libs.constraint.layout)
    implementation(libs.lottie.compose)

    androidTestImplementation(libs.junit5.compose)
    androidTestImplementation(libs.junit5.api)
    androidTestImplementation(libs.junit5.extensions)
    androidTestRuntimeOnly(libs.junit5.runner)
}
