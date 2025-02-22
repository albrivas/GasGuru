import io.gitlab.arturbosch.detekt.Detekt

plugins {
    alias(libs.plugins.android.application) apply false
    alias(libs.plugins.kotlin.serialization) apply false
    alias(libs.plugins.android.library) apply false
    alias(libs.plugins.kotlin.android) apply false
    alias(libs.plugins.ksp) apply false
    alias(libs.plugins.kotlin.jvm) apply false
    alias(libs.plugins.hilt.gradle) apply false
    alias(libs.plugins.gms) apply false
    alias(libs.plugins.firebase.crashlitycs) apply false
    alias(libs.plugins.compose.compiler) apply false
    alias(libs.plugins.detekt) apply false
    alias(libs.plugins.secrets) apply false
    alias(libs.plugins.junit5) apply false
    alias(libs.plugins.room) apply false
}

allprojects {
    val detektRootPath = "$rootDir/config/detekt"
    val detektFilePath = "$detektRootPath/detekt.yml"
    val detektReportPath = "$detektRootPath/detekt_report.html"
    val detektTaskName = "codeCheck"

    tasks.register<Detekt>(detektTaskName) {
        group = "detekt"
        description = "Runs a custom detekt build"

        setSource(
            files(
                "src/main/java",
                "src/test/java",
                "src/main/kotlin",
                "src/test/kotlin",
                "src/extended/java",
                "src/external/java",
            )
        )

        config.setFrom(file(detektFilePath))
        buildUponDefaultConfig = true
        autoCorrect = true
        debug = true

        reports {
            html {
                required.set(true)
                outputLocation.set(file(detektReportPath))
            }
        }

        doFirst { println(message = "DETEKT ----> Running custom detekt build") }
        //dependsOn(":sdk:test")
    }
}