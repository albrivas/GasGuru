plugins {
    alias(libs.plugins.gasguru.kmp.library)
    alias(libs.plugins.gasguru.koin)
}

android {
    namespace = "com.gasguru.core.domain"
}

kotlin {
    sourceSets {
        commonMain.dependencies {
            implementation(projects.core.data)
            implementation(projects.core.model)
            implementation(projects.core.common)
            implementation(projects.core.notifications)
        }
        commonTest.dependencies {
            implementation(kotlin("test"))
            implementation(libs.kotlinx.coroutines.test)
            implementation(libs.turbine)
        }
    }
}
