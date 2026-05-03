plugins {
    alias(libs.plugins.gasguru.kmp.compose.library)
    alias(libs.plugins.gasguru.koin)
    alias(libs.plugins.gasguru.proguard)
}

compose.resources {
    publicResClass = true
    packageOfResClass = "com.gasguru.core.components.generated.resources"
}

kotlin {
    sourceSets {
        commonMain.dependencies {
            implementation(projects.core.analytics)
            implementation(projects.core.ui)
            implementation(projects.core.domain)
            implementation(projects.core.model)
            implementation(projects.core.uikit)
            implementation(projects.core.common)
        }
        commonTest.dependencies {
            implementation(kotlin("test"))
            implementation(libs.kotlinx.coroutines.test)
            implementation(libs.turbine)
            implementation(projects.core.testing)
        }
        androidUnitTest.dependencies {
            implementation(libs.junit5.api)
            implementation(libs.junit5.engine)
            implementation(libs.junit5.extensions)
        }
    }
}

android {
    namespace = "com.gasguru.core.components"
}