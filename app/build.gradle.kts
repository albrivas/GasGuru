import com.android.build.gradle.internal.cxx.configure.gradleLocalProperties

plugins {
    alias(libs.plugins.gasguru.android.application)
    alias(libs.plugins.gasguru.koin)
    alias(libs.plugins.gasguru.firebase)
    alias(libs.plugins.kotlin.serialization)
    alias(libs.plugins.gms)
    alias(libs.plugins.compose.compiler)
    alias(libs.plugins.gasguru.flavors)
    alias(libs.plugins.gasguru.jacoco)
    alias(libs.plugins.gasguru.secrets.google)
    alias(libs.plugins.kotlin.parcelize)
    alias(libs.plugins.stability.analyzer)
}

tasks.withType<Test>().configureEach {
    useJUnitPlatform()
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
            isMinifyEnabled = true
            isShrinkResources = true
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

    implementation(projects.core.analytics)
    implementation(projects.core.uikit)
    implementation(projects.core.ui)
    implementation(projects.core.data)
    implementation(projects.core.database)
    implementation(projects.core.domain)
    implementation(projects.core.common)
    implementation(projects.core.components)
    implementation(projects.core.supabase)
    implementation(projects.feature.onboarding)
    implementation(projects.feature.vehicle)
    implementation(projects.feature.detailStation)
    implementation(projects.feature.favoriteListStation)
    implementation(projects.feature.stationMap)
    implementation(projects.feature.profile)
    implementation(projects.core.model)
    implementation(projects.auto.common)
    implementation(projects.core.network)
    implementation(projects.feature.search)
    implementation(projects.feature.routePlanner)
    implementation(projects.feature.widget)
    implementation(projects.navigation)
    implementation(projects.core.notifications)
    mockImplementation(projects.mocknetwork)
    androidTestImplementation(projects.core.testing)

    // Core Android dependencies
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)

    // Arch Components
    implementation(libs.androidx.lifecycle.viewmodel.compose)
    implementation(libs.jetbrains.navigation.compose)
    implementation(libs.koin.androidx.compose)

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
    implementation(libs.androidx.work.runtime.ktx)
    implementation(libs.androidx.glance.appwidget)

    // Analytics
    implementation(libs.mixpanel)
    implementation(libs.onesignal)
    implementation(libs.clarity.compose)

    testImplementation(projects.core.testing)
    testImplementation(libs.koin.test.junit5)
    testImplementation(libs.mockk)
    testRuntimeOnly(libs.junit5.engine)
}
