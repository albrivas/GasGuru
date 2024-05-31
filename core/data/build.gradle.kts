@Suppress("DSL_SCOPE_VIOLATION") // Remove when fixed https://youtrack.jetbrains.com/issue/KTIJ-19369
plugins {
    alias(libs.plugins.android.library)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.kapt)
}

android {
    namespace = "com.albrivas.fuelpump.core.data"
    compileSdk = 34

    defaultConfig {
        minSdk = 23

        testInstrumentationRunner = "com.albrivas.fuelpump.core.testing.HiltTestRunner"
        consumerProguardFiles("consumer-rules.pro")
    }

    buildFeatures {
        aidl = false
        buildConfig = false
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
    implementation(project(":core:database"))
    implementation(project(":core:network"))
    implementation(project(":core:model"))
    implementation(project(":core:common"))

    testImplementation(project(":core:testing"))

    // Arch Components
    implementation(libs.hilt.android)
    kapt(libs.hilt.compiler)

    implementation(libs.kotlinx.coroutines.android)
    implementation(libs.kotlin.coroutines.play)
    implementation(libs.io.arrow.kt.arrow.core)
    implementation(libs.play.services.location)
}
