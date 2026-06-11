import com.codingfeline.buildkonfig.compiler.FieldSpec
import java.util.Properties

plugins {
    alias(libs.plugins.gasguru.kmp.library)
    alias(libs.plugins.gasguru.koin)
    alias(libs.plugins.gasguru.proguard)
    alias(libs.plugins.buildkonfig)
}

android {
    namespace = "com.gasguru.core.analytics"

    testOptions {
        unitTests.isReturnDefaultValues = true
    }

    buildFeatures {
        buildConfig = true
    }
}

val localProps = Properties().apply {
    rootProject.file("local.properties").takeIf { it.exists() }
        ?.inputStream()
        ?.use { load(it) }
}

buildkonfig {
    packageName = "com.gasguru.core.analytics"
    objectName = "AnalyticsSecrets"

    val mixpanelToken = localProps.getProperty("mixpanelProjectToken")
        ?: System.getenv("MIXPANEL").orEmpty()

    defaultConfigs {
        buildConfigField(FieldSpec.Type.STRING, "MIXPANEL_TOKEN", mixpanelToken)
    }
}

kotlin {
    sourceSets {
        commonMain {
            kotlin.srcDir(tasks.named("generateBuildKonfig"))
            dependencies {
                // AnalyticsEvent, AnalyticsHelper, NoOpAnalyticsHelper — Kotlin puro
            }
        }
        androidMain.dependencies {
            implementation(libs.androidx.compose.runtime)
            implementation(libs.mixpanel)
        }
        commonTest.dependencies {
            implementation(kotlin("test"))
        }
    }
}

dependencies {
    // Tests Android-específicos: LogcatAnalyticsHelperTest, MixpanelAnalyticsHelperTest
    testImplementation(libs.mockk)
    testImplementation(libs.json)
    testImplementation(libs.junit5.api)
    testRuntimeOnly(libs.junit5.engine)
}
