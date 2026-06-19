plugins {
    alias(libs.plugins.gasguru.kmp.library)
}

android {
    namespace = "com.gasguru.core.model"
}

kotlin {
    sourceSets {
        commonTest.dependencies {
            implementation(libs.kotlin.test)
            implementation(libs.kotest.assertions.core)
        }
    }
}
