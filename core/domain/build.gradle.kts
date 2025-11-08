plugins {
    alias(libs.plugins.gasguru.android.library)
    alias(libs.plugins.gasguru.hilt)
    alias(libs.plugins.gasguru.proguard)
}

android {
    namespace = "com.gasguru.core.domain"
}

dependencies {
    implementation(projects.core.data)
    implementation(projects.core.model)
    implementation(projects.core.common)
    implementation(projects.core.notifications)
    implementation(libs.androidx.core.ktx)

    testImplementation(projects.core.testing)
}