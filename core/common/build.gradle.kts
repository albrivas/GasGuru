import com.google.android.libraries.mapsplatform.secrets_gradle_plugin.loadPropertiesFile

plugins {
    alias(libs.plugins.gasguru.android.library)
    alias(libs.plugins.gasguru.hilt)
    alias(libs.plugins.gasguru.proguard)
}

val versionProperties = loadPropertiesFile("../../versions.properties")
val versionMajor: String = versionProperties.getProperty("versionMajor")
val versionMinor: String = versionProperties.getProperty("versionMinor")
val versionPatch: String = versionProperties.getProperty("versionPatch")
val codeVersion: String = versionProperties.getProperty("versionCode")

android {
    namespace = "com.gasguru.core.common"

    defaultConfig {
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

    buildFeatures {
        buildConfig = true
    }
}

dependencies {
    implementation(projects.core.model)
    implementation(libs.androidx.core.ktx)
    implementation(libs.appcompat)
    implementation(libs.material)
    api(libs.play.services.maps)
    implementation(libs.maps.compose)
    
    testImplementation(libs.junit)
}