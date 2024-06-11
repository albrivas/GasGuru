plugins {
    alias(libs.plugins.android.library)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.compose.compiler)
    alias(libs.plugins.kotlin.serialization)
    alias(libs.plugins.ksp)
    alias(libs.plugins.secrets)
}

android {
    namespace = "com.albrivas.feature.station_map"
    compileSdk = 34

    defaultConfig {
        minSdk = 23

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildFeatures {
        compose = true
        aidl = false
        buildConfig = true
        renderScript = false
        shaders = false
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
}

dependencies {
    implementation(project(":core:data"))
    implementation(project(":core:ui"))
    implementation(project(":core:domain"))
    implementation(project(":core:model"))
    implementation(project(":core:uikit"))
    implementation(project(":core:common"))
    androidTestImplementation(project(":core:testing"))

    implementation(libs.androidx.activity.compose)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.lifecycle.viewmodel.compose)
    implementation(libs.androidx.hilt.navigation.compose)
    implementation(libs.androidx.lifecycle.runtimeCompose)
    implementation(libs.androidx.compose.ui)
    implementation(libs.androidx.compose.ui.tooling.preview)
    implementation(libs.androidx.compose.material3)
    implementation(libs.androidx.compose.permission)
    implementation(libs.kotlinx.serialization.json)
    implementation(libs.androidx.navigation.compose)
    implementation(libs.hilt.android)
    ksp(libs.hilt.compiler)
    implementation(libs.kotlin.coroutines.play)
    debugImplementation(libs.androidx.compose.ui.tooling)

    implementation(libs.maps.compose)
    implementation(libs.play.services.maps)
    implementation(libs.places)

    androidTestImplementation(libs.androidx.compose.ui.test.junit4)
    debugImplementation(libs.androidx.compose.ui.test.manifest)
}