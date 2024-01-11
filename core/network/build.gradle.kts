/*
 * File: build.gradle.kts
 * Project: FuelPump
 * Module: FuelPump.core.network
 * Last modified: 12/29/22, 4:59 PM
 *
 * Created by albertorivas on 12/29/22, 5:33 PM
 * Copyright © 2022 Alberto Rivas. All rights reserved.
 *
 */

@Suppress("DSL_SCOPE_VIOLATION") // Remove when fixed https://youtrack.jetbrains.com/issue/KTIJ-19369
plugins {
    alias(libs.plugins.android.library)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.kapt)
    alias(libs.plugins.kotlin.serialization)
    alias(libs.plugins.ksp)
}

android {
    namespace = "com.albrivas.fuelpump.core.network"
    compileSdk = 34

    defaultConfig {
        minSdk = 23
        targetSdk = 34

        consumerProguardFiles("consumer-rules.pro")
    }

    buildFeatures {
        buildConfig = true
    }

    testOptions {
        unitTests {
            isIncludeAndroidResources = true
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }

    kotlinOptions {
        jvmTarget = JavaVersion.VERSION_17.toString()
    }

    val baseUrl = "\"https://sedeaplicaciones.minetur.gob.es/\""

    buildTypes {
        release {
            buildConfigField("String", "BASE_URL", baseUrl)
            buildConfigField(
                "okhttp3.logging.HttpLoggingInterceptor.Level",
                "LEVEL_LOGS",
                "okhttp3.logging.HttpLoggingInterceptor.Level.NONE"
            )
        }
        debug {
            buildConfigField("String", "BASE_URL", baseUrl)
            buildConfigField(
                "okhttp3.logging.HttpLoggingInterceptor.Level",
                "LEVEL_LOGS",
                "okhttp3.logging.HttpLoggingInterceptor.Level.BODY"
            )
        }
    }
    sourceSets {
        getByName("main") {
            resources {
                srcDirs("src/main/resources", "src/test/resources")
            }
        }
    }
}

dependencies {
    testImplementation(project(":core:testing"))

    implementation(libs.hilt.android)
    kapt(libs.hilt.compiler)

    implementation(libs.bundles.moshi)
    ksp(libs.moshi.codegen)

    implementation(libs.bundles.com.squareup.retrofit2)
    implementation(libs.kotlinx.coroutines.android)
    implementation(libs.kotlinx.serialization.json)
    implementation(libs.io.arrow.kt.arrow.core)

    // Local tests: jUnit, coroutines, Android runner
    testImplementation(libs.junit)
    testImplementation(libs.kotlinx.coroutines.test)
    testImplementation(libs.mock.webserver)
}
