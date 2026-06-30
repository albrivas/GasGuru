plugins {
    alias(libs.plugins.gasguru.kmp.compose.library)
}

android {
    namespace = "com.gasguru.core.ui"
}

compose.resources {
    publicResClass = true
    packageOfResClass = "com.gasguru.core.ui.generated.resources"
}

dependencies {
    debugImplementation(compose.uiTooling)
}

kotlin {
    sourceSets {
        commonMain.dependencies {
            api(projects.core.model)
            api(projects.core.uikit)
            implementation(projects.core.analytics)
        }
        androidMain.dependencies {
            implementation(libs.androidx.core.ktx)
            implementation(libs.play.review.ktx)
        }
        commonTest.dependencies {
            implementation(libs.kotlin.test)
            implementation(libs.kotlinx.coroutines.test)
        }
    }
}
