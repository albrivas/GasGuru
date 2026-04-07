plugins {
    alias(libs.plugins.gasguru.kmp.library)
    alias(libs.plugins.gasguru.kmp.room)
    alias(libs.plugins.gasguru.koin)
    alias(libs.plugins.kotlin.serialization)
}

android {
    namespace = "com.gasguru.core.database"
}

kotlin {
    sourceSets {
        commonMain.dependencies {
            implementation(projects.core.model)
            implementation(libs.kotlinx.coroutines.core)
            implementation(libs.kotlinx.serialization.json)
        }
        androidMain.dependencies {
            implementation(libs.kotlinx.coroutines.android)
        }
        commonTest.dependencies {
            implementation(kotlin("test"))
            implementation(libs.kotest.assertions.core)
        }
        androidUnitTest.dependencies {
            implementation(libs.mockk)
            implementation(libs.junit5.api)
            runtimeOnly(libs.junit5.engine)
            implementation(libs.kotlinx.coroutines.test)
            implementation(libs.turbine)
            implementation(libs.androidx.sqlite.bundled)
            implementation(libs.androidx.room.runtime)
        }
    }
}
