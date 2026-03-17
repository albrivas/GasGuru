plugins {
    alias(libs.plugins.gasguru.android.library)
    alias(libs.plugins.gasguru.compose.library)
    alias(libs.plugins.gasguru.koin)
    alias(libs.plugins.gasguru.proguard)
    alias(libs.plugins.junit5)
}

android {
    namespace = "com.gasguru.core.analytics"

    testOptions {
        unitTests.isReturnDefaultValues = true
    }
}

dependencies {
    implementation(libs.mixpanel)

    testImplementation(libs.mockk)
    testImplementation(libs.json)
    testImplementation(libs.junit5.api)
    testRuntimeOnly(libs.junit5.engine)
}
