plugins {
    alias(libs.plugins.android.library)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.ksp)
    alias(libs.plugins.detekt)
}

android {
    namespace = "com.gasguru.core.data"
    compileSdk = 35

    defaultConfig {
        minSdk = 26

        testInstrumentationRunner = "com.gasguru.core.testing.HiltTestRunner"
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
    implementation(projects.core.database)
    implementation(projects.core.network)
    implementation(projects.core.model)
    implementation(projects.core.common)
    implementation(libs.hilt.android)
    ksp(libs.hilt.compiler)
    detektPlugins(libs.detekt.formatting)
    implementation(libs.kotlinx.coroutines.android)
    implementation(libs.kotlin.coroutines.play)
    implementation(libs.io.arrow.kt.arrow.core)
    implementation(libs.play.services.location)
    implementation(libs.places)
    testImplementation(projects.core.testing)
}
