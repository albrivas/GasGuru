plugins {
    alias(libs.plugins.android.library)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.serialization)
    alias(libs.plugins.compose.compiler)
    alias(libs.plugins.ksp)
    alias(libs.plugins.detekt)
}

android {
    namespace = "com.gasguru.feature.favorite_list_station"
    compileSdk = 35

    defaultConfig {
        minSdk = 26

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        testInstrumentationRunnerArguments["runnerBuilder"] = "de.mannodermaus.junit5.AndroidJUnit5Builder"
    }
    buildFeatures {
        compose = true
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
    implementation(project(":core:ui"))
    implementation(project(":core:domain"))
    implementation(project(":core:model"))
    implementation(project(":core:uikit"))
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
    detektPlugins(libs.detekt.formatting)

    //androidTestImplementation(libs.androidx.compose.ui.test.junit4)
    debugImplementation(libs.androidx.compose.ui.test.manifest)
    debugImplementation(libs.androidx.compose.ui.tooling)

    androidTestImplementation(libs.junit5.compose)
    androidTestImplementation(libs.junit5.api)
    androidTestImplementation(libs.junit5.extensions)
    androidTestRuntimeOnly(libs.junit5.runner)

    // Hilt Dependency Injection
    implementation(libs.hilt.android)
    ksp(libs.hilt.compiler)
}