plugins {
    alias(libs.plugins.gasguru.android.library)
    alias(libs.plugins.gasguru.koin)
    alias(libs.plugins.gasguru.proguard)
}

android {
    namespace = "com.gasguru.core.notifications"
}

dependencies {
    implementation(libs.onesignal)
    implementation(libs.kotlinx.coroutines.android)
    implementation(libs.io.arrow.kt.arrow.core)

    testImplementation(projects.core.testing)
}