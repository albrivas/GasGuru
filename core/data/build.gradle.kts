import com.codingfeline.buildkonfig.compiler.FieldSpec
import java.util.Properties

plugins {
    alias(libs.plugins.gasguru.kmp.library)
    alias(libs.plugins.gasguru.koin)
    alias(libs.plugins.gasguru.proguard)
    alias(libs.plugins.gasguru.secrets.google)
    alias(libs.plugins.buildkonfig)
    kotlin("native.cocoapods")
}

android {
    namespace = "com.gasguru.core.data"
    testOptions {
        unitTests.isReturnDefaultValues = true
    }
}

val localProps = Properties().apply {
    rootProject.file("local.properties").takeIf { it.exists() }
        ?.inputStream()
        ?.use { load(it) }
}

buildkonfig {
    packageName = "com.gasguru.core.data"
    objectName = "DataSecrets"

    defaultConfigs {
        val googleApiKey = localProps.getProperty("googleApiKey")
            ?: System.getenv("GOOGLE_API_KEY").orEmpty()
        buildConfigField(FieldSpec.Type.STRING, "GOOGLE_API_KEY", googleApiKey)

        val placesApiKeyIos = localProps.getProperty("placesApiKeyIos")
            ?: System.getenv("PLACES_API_KEY_IOS").orEmpty()
        buildConfigField(FieldSpec.Type.STRING, "PLACES_API_KEY_IOS", placesApiKeyIos)
    }
}

kotlin {
    cocoapods {
        summary = "GasGuru core data layer"
        homepage = "https://github.com/gasguru/GasGuru"
        version = "1.0"
        ios.deploymentTarget = "15.0"

        pod("GooglePlaces") {
            version = "~> 8.5"
        }
    }

    sourceSets {
        commonMain {
            kotlin.srcDir(tasks.named("generateBuildKonfig"))
            dependencies {
                implementation(projects.core.analytics)
                implementation(projects.core.database)
                implementation(projects.core.model)
                implementation(projects.core.common)
                implementation(projects.core.supabase)
                implementation(projects.core.notifications)
                implementation(libs.io.arrow.kt.arrow.core)
                implementation(libs.kotlinx.coroutines.core)
            }
        }
        androidMain.dependencies {
            implementation(projects.core.network)
            implementation(libs.kotlinx.coroutines.android)
            implementation(libs.kotlin.coroutines.play)
            implementation(libs.play.services.location)
            implementation(libs.places)
            implementation(libs.maps.utils)
        }
        commonTest.dependencies {
            implementation(kotlin("test"))
            implementation(libs.kotlinx.coroutines.test)
            implementation(libs.turbine)
        }
        androidUnitTest.dependencies {
            implementation(projects.core.testing)
            runtimeOnly(libs.junit5.engine)
        }
    }
}
