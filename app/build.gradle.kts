import io.gitlab.arturbosch.detekt.Detekt

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.hilt.gradle)
    alias(libs.plugins.kotlin.serialization)
    alias(libs.plugins.gms)
    alias(libs.plugins.firebase.crashlitycs)
    alias(libs.plugins.compose.compiler)
    alias(libs.plugins.ksp)
    alias(libs.plugins.detekt)
    alias(libs.plugins.secrets)
}

android {
    namespace = "com.albrivas.fuelpump"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.albrivas.fuelpump"
        minSdk = 26
        targetSdk = 34
        versionCode = 9
        versionName = "1.0.8"

        testInstrumentationRunner = "com.albrivas.fuelpump.core.testing.HiltTestRunner"
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
        }
        debug {
            isMinifyEnabled = false
            isDebuggable = true
            applicationIdSuffix = ".debug"
            resValue("string", "app_name", "FuelPump Debug")
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }

    kotlinOptions {
        jvmTarget = JavaVersion.VERSION_17.toString()
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
val detektRootPath = "$rootDir/config/detekt"
val detektFilePath = "$detektRootPath/detekt.yml"
val detektReportPath = "$detektRootPath/detekt_report.html"
val detektTaskName = "codeCheck"

tasks.register<Detekt>("codeCheck") {
    group = "detekt"
    description = "Runs a custom detekt build"

    setSource(
        files(
            "src/main/java",
            "src/test/java",
            "src/main/kotlin",
            "src/test/kotlin",
            "src/extended/java",
            "src/external/java"
        )
    )
    config.setFrom(file(detektFilePath))
    buildUponDefaultConfig = true
    autoCorrect = true
    debug = true

    reports {
        html {
            required.set(true)
            outputLocation.set(file(detektReportPath))
        }
    }

    doFirst { println(message = "DETEKT ----> Running custom detekt build") }
    //dependsOn(":sdk:test")
}

dependencies {

    implementation(project(":core:uikit"))
    implementation(project(":core:ui"))
    implementation(project(":core:data"))
    implementation(project(":feature:splash"))
    implementation(project(":feature:onboarding"))
    implementation(project(":feature:detail-station"))
    implementation(project(":feature:fuel-list-station"))
    implementation(project(":feature:station-map"))
    implementation(project(":core:model"))
    androidTestImplementation(project(":core:testing"))
    detektPlugins(libs.detekt.formatting)

    // Core Android dependencies
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)

    // Hilt Dependency Injection
    implementation(libs.hilt.android)
    ksp(libs.hilt.compiler)

    // Arch Components
    implementation(libs.androidx.lifecycle.viewmodel.compose)
    implementation(libs.androidx.navigation.compose)
    implementation(libs.androidx.hilt.navigation.compose)

    // Compose
    implementation(libs.androidx.compose.ui)
    implementation(libs.androidx.compose.ui.tooling.preview)
    implementation(libs.androidx.compose.material3)
    implementation(libs.kotlinx.serialization.json)

    // Firebase
    implementation(platform(libs.firebase.bom))
    implementation(libs.firebase.crashlytics)
    implementation(libs.firebase.analytics)
}
