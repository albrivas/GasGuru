plugins {
    alias(libs.plugins.gasguru.android.library)
    alias(libs.plugins.gasguru.compose.library)
}

android {
    namespace = "com.gasguru.core.ui"
}

dependencies {
    implementation(projects.core.model)
    implementation(projects.core.uikit)
    implementation(libs.androidx.core.ktx)
}
