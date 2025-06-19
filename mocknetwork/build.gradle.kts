plugins {
    alias(libs.plugins.gasguru.android.library)
    alias(libs.plugins.gasguru.hilt)
}

android {
    namespace = "com.gasguru.mocknetwork"
}

dependencies {
    implementation(projects.core.network)
    implementation(projects.core.common)
    implementation(libs.bundles.moshi)
    ksp(libs.moshi.codegen)
    implementation(libs.bundles.com.squareup.retrofit2)
    implementation(libs.mock.webserver)
    implementation(libs.io.arrow.kt.arrow.core)
}
