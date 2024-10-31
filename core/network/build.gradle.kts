plugins {
    alias(libs.plugins.android.library)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.serialization)
    alias(libs.plugins.ksp)
    alias(libs.plugins.secrets)
    alias(libs.plugins.detekt)
}

android {
    namespace = "com.gasguru.core.network"
    compileSdk = 35

    defaultConfig {
        minSdk = 26

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

    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
            merges += "META-INF/LICENSE.md"
            merges += "META-INF/LICENSE-notice.md"
        }
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
    implementation(libs.hilt.android)
    ksp(libs.hilt.compiler)
    implementation(libs.bundles.moshi)
    ksp(libs.moshi.codegen)
    implementation(libs.bundles.com.squareup.retrofit2)
    implementation(libs.kotlinx.coroutines.android)
    implementation(libs.kotlinx.serialization.json)
    implementation(libs.io.arrow.kt.arrow.core)
    implementation(libs.kotlin.coroutines.play)
    implementation(libs.places)
    detektPlugins(libs.detekt.formatting)
    testImplementation(libs.junit)
    testImplementation(libs.kotlinx.coroutines.test)
    testImplementation(libs.mock.webserver)
    testImplementation(project(":core:testing"))
}
