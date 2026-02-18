plugins {
    alias(libs.plugins.gasguru.kmp.library)
    alias(libs.plugins.kotlin.serialization)
    alias(libs.plugins.gasguru.secrets.google)
    alias(libs.plugins.gasguru.proguard)
    alias(libs.plugins.ksp)
}

android {
    namespace = "com.gasguru.core.network"
}

kotlin {
    sourceSets {
        commonMain.dependencies {
            implementation(libs.ktor.client.core)
            implementation(libs.ktor.client.content.negotiation)
            implementation(libs.ktor.client.logging)
            implementation(libs.ktor.serialization.kotlinx.json)
            implementation(libs.kotlinx.serialization.json)
            implementation(libs.kotlinx.coroutines.core)
            implementation(libs.io.arrow.kt.arrow.core)
        }
        androidMain.dependencies {
            implementation(libs.ktor.client.okhttp)
            implementation(libs.kotlinx.coroutines.android)
            implementation(libs.places)
            implementation(libs.kotlin.coroutines.play)
            implementation(libs.hilt.android)
            implementation(libs.bundles.moshi)
            implementation(libs.bundles.com.squareup.retrofit2)
        }
        iosMain.dependencies {
            implementation(libs.ktor.client.darwin)
        }
        commonTest.dependencies {
            implementation(libs.ktor.client.mock)
            implementation(kotlin("test"))
        }
    }
}

dependencies {
    add("kspAndroid", libs.hilt.compiler)
    add("kspAndroid", libs.moshi.codegen)
}