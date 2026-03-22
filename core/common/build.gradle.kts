import com.google.android.libraries.mapsplatform.secrets_gradle_plugin.loadPropertiesFile

plugins {
    alias(libs.plugins.gasguru.kmp.library)
    alias(libs.plugins.gasguru.koin)
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

    buildFeatures {
        buildConfig = true
    }
}

kotlin {
    sourceSets {
        commonMain.dependencies {
            implementation(projects.core.model)
            implementation(libs.kotlinx.coroutines.core)
            implementation(libs.koin.core)
            api(libs.kotlinx.datetime)
        }
        androidMain.dependencies {
            implementation(libs.androidx.core.ktx)
            implementation(libs.appcompat)
            implementation(libs.material)
            api(libs.play.services.maps)
            implementation(libs.maps.compose)
            implementation(libs.koin.android)
        }
        commonTest.dependencies {
            implementation(kotlin("test"))
            implementation(libs.kotest.assertions.core)
        }
    }
}
