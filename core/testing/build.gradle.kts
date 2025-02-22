plugins {
    alias(libs.plugins.android.library)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.detekt)
    alias(libs.plugins.junit5)
}

android {
    namespace = "com.gasguru.core.testing"
    compileSdk = 35

    defaultConfig {
        minSdk = 26

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        testInstrumentationRunnerArguments["runnerBuilder"] = "de.mannodermaus.junit5.AndroidJUnit5Builder"
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

    implementation(project(":core:data"))
    detektPlugins(libs.detekt.formatting)

    api(libs.junit)
    api(libs.androidx.test.core)
    api(libs.kotlinx.coroutines.test)

    api(libs.androidx.test.espresso.core)
    api(libs.androidx.test.runner)
    api(libs.androidx.test.rules)
    //api(libs.androidx.compose.ui.test)
    api(libs.hilt.android.testing)
    api(libs.bundles.testing)

    api(libs.androidx.compose.ui.test.manifest)

    api(libs.androidx.compose.ui.test.manifest)
    api(libs.androidx.compose.ui.tooling)

    api(libs.junit5.compose)
    api(libs.junit5.api)
    api(libs.junit5.extensions)
    api(libs.junit5.runner)
}
