plugins {
    alias(libs.plugins.gasguru.android.library)
    alias(libs.plugins.gasguru.compose.library)
    alias(libs.plugins.gasguru.koin)
    alias(libs.plugins.gasguru.proguard)
}

android {
    namespace = "com.gasguru.core.analytics"

    testOptions {
        unitTests.isReturnDefaultValues = true
    }
}

dependencies {
    implementation(libs.mixpanel)

    testImplementation(libs.junit)
    testImplementation(libs.mockk)
}
