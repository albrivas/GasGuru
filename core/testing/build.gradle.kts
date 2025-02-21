plugins {
    alias(libs.plugins.gasguru.android.library)
    alias(libs.plugins.junit5)
}

android {
    namespace = "com.gasguru.core.testing"
}

dependencies {
    api(projects.core.data)
    api(libs.junit)
    api(libs.androidx.test.core)
    api(libs.kotlinx.coroutines.test)
    api(libs.androidx.test.espresso.core)
    api(libs.androidx.test.runner)
    api(libs.androidx.test.rules)
    api(libs.hilt.android.testing)
    api(libs.bundles.testing)
    api(libs.androidx.compose.ui.test.manifest)
    api(libs.androidx.compose.ui.test.manifest)
    api(libs.androidx.compose.ui.tooling)
    api(libs.junit5.compose)
    api(libs.junit5.api)
    api(libs.junit5.extensions)
    api(libs.junit5.runner)
}
