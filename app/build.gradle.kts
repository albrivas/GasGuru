import com.android.build.gradle.internal.cxx.configure.gradleLocalProperties

plugins {
    alias(libs.plugins.gasguru.android.application)
    alias(libs.plugins.gasguru.hilt)
    alias(libs.plugins.gasguru.firebase)
    alias(libs.plugins.kotlin.serialization)
    alias(libs.plugins.gms)
    alias(libs.plugins.compose.compiler)
    alias(libs.plugins.ksp)
    alias(libs.plugins.secrets)
    alias(libs.plugins.gasguru.flavors)
    alias(libs.plugins.gasguru.jacoco)
}

val localProperties = gradleLocalProperties(rootDir, providers)
val alias: String = localProperties.getProperty("keyAlias")
val storepass: String = localProperties.getProperty("storePassword")
val keypass: String = localProperties.getProperty("keyPassword")

android {
    namespace = "com.gasguru"

    signingConfigs {
        create("release") {
            storeFile = file("../keystore/fuelpump")
            storePassword = storepass
            keyAlias = alias
            keyPassword = keypass
        }
    }

    defaultConfig {
        applicationId = "com.gasguru"
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        vectorDrawables {
            useSupportLibrary = true
        }
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            isDebuggable = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
            signingConfig = signingConfigs.getByName("release")
            manifestPlaceholders["crashlyticsEnabled"] = true
        }
        debug {
            isMinifyEnabled = false
            isDebuggable = true
            applicationIdSuffix = ".debug"
            resValue("string", "app_name", "GasGuru Debug")
            manifestPlaceholders["crashlyticsEnabled"] = true
        }
    }

    buildFeatures {
        compose = true
        aidl = false
        renderScript = false
        shaders = false
        buildConfig = true
    }

    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
            merges += "META-INF/LICENSE.md"
            merges += "META-INF/LICENSE-notice.md"
        }
    }
}

dependencies {

    implementation(projects.core.uikit)
    implementation(projects.core.ui)
    implementation(projects.core.data)
    implementation(projects.core.domain)
    implementation(projects.core.common)
    implementation(projects.feature.onboarding)
    implementation(projects.feature.detailStation)
    implementation(projects.feature.favoriteListStation)
    implementation(projects.feature.stationMap)
    implementation(projects.feature.profile)
    implementation(projects.core.model)
    implementation(projects.auto.common)
    implementation(projects.core.network)
    implementation(projects.mocknetwork)
    androidTestImplementation(projects.core.testing)

    // Core Android dependencies
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)

    // Arch Components
    implementation(libs.androidx.lifecycle.viewmodel.compose)
    implementation(libs.androidx.navigation.compose)
    implementation(libs.androidx.hilt.navigation.compose)

    // Compose
    implementation(libs.androidx.compose.ui)
    implementation(libs.androidx.compose.ui.tooling.preview)
    implementation(libs.androidx.compose.material3)
    implementation(libs.kotlinx.serialization.json)
    implementation(libs.material.icons.extended)

    // Firebase
    implementation(platform(libs.firebase.bom))
    implementation(libs.firebase.crashlytics)
    implementation(libs.firebase.analytics)

    implementation(libs.splash.screen)
}
