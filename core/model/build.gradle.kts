@Suppress("DSL_SCOPE_VIOLATION")
plugins {
    alias(libs.plugins.android.library)
    alias(libs.plugins.kotlin.android)
}
////
android {
    namespace = "com.albrivas.fuelpump.core.model"
    compileSdk = 33

    defaultConfig {
        minSdk = 23
        targetSdk = 33
    }

    buildFeatures {
        compose = false
        aidl = false
        buildConfig = false
        renderScript = false
        shaders = false
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    kotlinOptions {
        jvmTarget = "1.8"
    }
}
