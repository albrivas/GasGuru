@file:OptIn(
    org.jetbrains.compose.ExperimentalComposeLibrary::class,
    org.jetbrains.kotlin.gradle.ExperimentalKotlinGradlePluginApi::class,
)

plugins {
    alias(libs.plugins.gasguru.kmp.compose.library)
}

android {
    namespace = "com.gasguru.core.uikit"
}

dependencies {
    debugImplementation(compose.uiTooling)
}

compose.resources {
    publicResClass = true
    packageOfResClass = "com.gasguru.core.uikit.generated.resources"
}

kotlin {
    androidTarget {
        instrumentedTestVariant.sourceSetTree.set(
            org.jetbrains.kotlin.gradle.plugin.KotlinSourceSetTree.test,
        )
    }

    sourceSets {
        commonMain.dependencies {
            implementation(compose.materialIconsExtended)
            implementation(libs.jetbrains.compose.ui.tooling.preview)
            implementation(libs.compottie)
            implementation(libs.compottie.resources)
        }
        androidMain.dependencies {
            implementation(libs.androidx.core.ktx)
        }
        commonTest.dependencies {
            implementation(libs.kotlin.test)
            implementation(compose.uiTest)
        }
        jvmTest.dependencies {
            implementation(compose.desktop.currentOs)
        }
    }
}

tasks.withType<Test>().configureEach {
    if (name != "jvmTest") {
        exclude(
            "**/GasGuruAlertDialogTest*",
            "**/TankCostCardTest*",
            "**/FuelListSelectionTest*",
            "**/SelectedItemTest*",
            "**/FuelStationItemTest*",
            "**/RouteNavigationCardTest*",
            "**/FuelTypeChipTest*",
            "**/NumberWheelPickerTest*",
        )
    }
}
