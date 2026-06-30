import com.codingfeline.buildkonfig.compiler.FieldSpec
import java.util.Properties

plugins {
    alias(libs.plugins.gasguru.kmp.library)
    alias(libs.plugins.gasguru.koin)
    alias(libs.plugins.gasguru.proguard)
    alias(libs.plugins.buildkonfig)
}

android {
    namespace = "com.gasguru.core.notifications"
}

val localProps = Properties().apply {
    rootProject.file("local.properties").takeIf { it.exists() }
        ?.inputStream()
        ?.use { load(it) }
}

buildkonfig {
    packageName = "com.gasguru.core.notifications"
    objectName = "NotificationsSecrets"

    val oneSignalAppId = localProps.getProperty("onesignalAppId")
        ?: System.getenv("ONESIGNAL_APP_ID").orEmpty()

    defaultConfigs {
        buildConfigField(FieldSpec.Type.STRING, "ONESIGNAL_APP_ID", oneSignalAppId)
    }
}

kotlin {
    sourceSets {
        commonMain {
            kotlin.srcDir(tasks.named("generateBuildKonfig"))
            dependencies {
                implementation(libs.kotlinx.coroutines.core)
                implementation(projects.core.analytics)
            }
        }
        androidMain.dependencies {
            implementation(libs.onesignal)
            implementation(libs.kotlinx.coroutines.android)
        }
        commonTest.dependencies {
            implementation(libs.kotlin.test)
            implementation(libs.kotlinx.coroutines.test)
        }
    }
}
