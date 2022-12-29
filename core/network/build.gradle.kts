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
    compileSdk = 33

    defaultConfig {
        minSdk = 23
        targetSdk = 33

        consumerProguardFiles("consumer-rules.pro")
    }

    buildFeatures {
        buildConfig = true
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }

    kotlinOptions {
        jvmTarget = "1.8"
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
}

dependencies {
    testImplementation(project(":core:testing"))

    implementation(libs.hilt.android)
    kapt(libs.hilt.compiler)

    implementation(libs.bundles.moshi)
    kapt(libs.moshi.codegen)

    implementation(libs.bundles.com.squareup.retrofit2)
    implementation(libs.kotlinx.coroutines.android)
    implementation(libs.kotlinx.serialization.json)
    implementation(libs.io.arrow.kt.arrow.core)

    // Local tests: jUnit, coroutines, Android runner
    testImplementation(libs.junit)
    testImplementation(libs.kotlinx.coroutines.test)
}
