import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    `kotlin-dsl`
}

group = "com.gasguru.buildlogic"

java {
    sourceCompatibility = JavaVersion.VERSION_17
    targetCompatibility = JavaVersion.VERSION_17
}

kotlin {
    compilerOptions {
        jvmTarget = JvmTarget.JVM_17
    }
}

dependencies {
    compileOnly(libs.android.gradlePlugin)
    compileOnly(libs.android.tools.common)
    compileOnly(libs.compose.gradlePlugin)
    compileOnly(libs.firebase.crashlytics.gradlePlugin)
    compileOnly(libs.kotlin.gradlePlugin)
    compileOnly(libs.ksp.gradlePlugin)
    compileOnly(libs.room.gradlePlugin)
    compileOnly(libs.secrets.gradlePlugin)
    compileOnly(libs.detekt.gradlePlugin)
    compileOnly(libs.sonarqube.gradlePlugin)
    compileOnly(libs.buildkonfig.gradlePlugin)
}

tasks {
    validatePlugins {
        enableStricterValidation = true
        failOnWarning = true
    }
}

gradlePlugin {
    plugins {
        register("androidApplication") {
            id = libs.plugins.gasguru.android.application.get().pluginId
            implementationClass = "AndroidApplicationConventionPlugin"
        }
        register("androidLibraryCompose") {
            id = libs.plugins.gasguru.compose.library.get().pluginId
            implementationClass = "AndroidLibraryComposeConventionPlugin"
        }
        register("androidLibrary") {
            id = libs.plugins.gasguru.android.library.get().pluginId
            implementationClass = "AndroidLibraryConventionPlugin"
        }
        register("hilt") {
            id = libs.plugins.gasguru.hilt.get().pluginId
            implementationClass = "HiltConventionPlugin"
        }
        register("room") {
            id = libs.plugins.gasguru.room.get().pluginId
            implementationClass = "AndroidRoomConventionPlugin"
        }
        register("firebase") {
            id = libs.plugins.gasguru.firebase.get().pluginId
            implementationClass = "AndroidApplicationFirebaseConventionPlugin"
        }
        register("environments") {
            id = libs.plugins.gasguru.flavors.get().pluginId
            implementationClass = "FlavorsConventionPlugin"
        }
        register("jacoco") {
            id = libs.plugins.gasguru.jacoco.get().pluginId
            implementationClass = "JacocoConventionPlugin"
        }
        register("secrets") {
            id = libs.plugins.gasguru.secrets.google.get().pluginId
            implementationClass = "SecretsConventionPlugin"
        }
        register("proguard") {
            id = libs.plugins.gasguru.proguard.get().pluginId
            implementationClass = "ProguardConventionPlugin"
        }
        register("sonar") {
            id = libs.plugins.gasguru.sonar.get().pluginId
            implementationClass = "SonarConventionPlugin"
        }
        register("kmpLibrary") {
            id = libs.plugins.gasguru.kmp.library.get().pluginId
            implementationClass = "KmpLibraryConventionPlugin"
        }
    }
}
