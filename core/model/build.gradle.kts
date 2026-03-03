plugins {
    alias(libs.plugins.gasguru.kmp.library)
}

android {
    namespace = "com.gasguru.core.model"
}

kotlin {
    sourceSets {
        commonMain {
            dependencies {
                implementation(libs.kotlinx.datetime)
            }
        }
        commonTest.dependencies {
            implementation(kotlin("test"))
        }
    }
}
