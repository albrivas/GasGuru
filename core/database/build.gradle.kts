plugins {
    alias(libs.plugins.gasguru.android.library)
    alias(libs.plugins.gasguru.koin)
    alias(libs.plugins.gasguru.room)
    alias(libs.plugins.junit5)
    alias(libs.plugins.gasguru.proguard)
}

android {
    namespace = "com.gasguru.core.database"
}

dependencies {
    implementation(projects.core.model)
    implementation(libs.kotlinx.coroutines.android)
    implementation(libs.moshi.kotlin)

    androidTestImplementation(libs.junit5.api)
    androidTestImplementation(libs.junit5.extensions)
    androidTestImplementation(libs.junit5.runner)
    androidTestRuntimeOnly(libs.junit5.engine)
    androidTestImplementation(libs.androidx.test.core)
    androidTestImplementation(libs.kotlinx.coroutines.test)
    androidTestImplementation(libs.turbine)
}
