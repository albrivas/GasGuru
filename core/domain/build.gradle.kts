plugins {
    alias(libs.plugins.gasguru.android.library)
    alias(libs.plugins.gasguru.hilt)
}

android {
    namespace = "com.gasguru.core.domain"
}

dependencies {
    implementation(projects.core.data)
    implementation(projects.core.model)
    implementation(projects.core.common)
    implementation(libs.androidx.core.ktx)

    testImplementation(projects.core.testing)
    androidTestRuntimeOnly(libs.junit5.runner)
}