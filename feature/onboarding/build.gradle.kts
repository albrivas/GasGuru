plugins {
    alias(libs.plugins.gasguru.android.library)
    alias(libs.plugins.gasguru.koin)
    alias(libs.plugins.gasguru.compose.library)
    alias(libs.plugins.gasguru.proguard)
    alias(libs.plugins.stability.analyzer)
}

android {
    namespace = "com.gasguru.feature.onboarding"
}

dependencies {
    implementation(projects.core.ui)
    implementation(projects.core.domain)
    implementation(projects.core.model)
    implementation(projects.core.uikit)
    implementation(projects.navigation)
    implementation(libs.koin.androidx.compose)

    testImplementation(projects.core.testing)
    testImplementation(libs.junit5.api)
    testImplementation(libs.junit5.extensions)
    testImplementation(libs.kotlinx.coroutines.test)
    testRuntimeOnly(libs.junit5.engine)

    androidTestImplementation(projects.core.testing)
    androidTestImplementation(libs.junit5.api)
    androidTestImplementation(libs.junit5.extensions)
}
