import com.google.android.libraries.mapsplatform.secrets_gradle_plugin.loadPropertiesFile

plugins {
    alias(libs.plugins.android.library)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.ksp)
    alias(libs.plugins.detekt)
}

val versionProperties = loadPropertiesFile("../../versions.properties")
val versionMajor: String = versionProperties.getProperty("versionMajor")
val versionMinor: String = versionProperties.getProperty("versionMinor")
val versionPatch: String = versionProperties.getProperty("versionPatch")
val codeVersion: String = versionProperties.getProperty("versionCode")

android {
    namespace = "com.gasguru.core.common"
    compileSdk = 35

    defaultConfig {
        minSdk = 26

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        buildConfigField("Integer", "versionMajor", versionMajor)
        buildConfigField("Integer", "versionMinor", versionMinor)
        buildConfigField("Integer", "versionPatch", versionPatch)
        buildConfigField("Integer", "versionCode", codeVersion)
    }

    buildTypes {
        release {
            isMinifyEnabled = false
        }
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

    buildFeatures {
        aidl = false
        buildConfig = true
        renderScript = false
        shaders = false
    }
}

dependencies {

    implementation(libs.androidx.core.ktx)
    implementation(libs.appcompat)
    implementation(libs.material)
    implementation(libs.hilt.android)
    ksp(libs.hilt.compiler)
    implementation(libs.play.services.maps)
    implementation(libs.maps.compose)
    detektPlugins(libs.detekt.formatting)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.test.ext.junit)
    androidTestImplementation(libs.androidx.test.espresso.core)
}