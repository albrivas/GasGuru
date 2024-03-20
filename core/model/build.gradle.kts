plugins {
    alias(libs.plugins.fuelpump.android.library)
    alias(libs.plugins.kotlin.android)
}

android {
    namespace = "com.albrivas.fuelpump.core.model"
    compileSdk = 34

    defaultConfig {
        minSdk = 23
        targetSdk = 34
    }

    buildFeatures {
        compose = false
        aidl = false
        buildConfig = false
        renderScript = false
        shaders = false
    }

    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
            merges += "META-INF/LICENSE.md"
            merges += "META-INF/LICENSE-notice.md"
        }
    }
}
