plugins {
    alias(libs.plugins.gasguru.kmp.library)
    alias(libs.plugins.junit5)
}

android {
    namespace = "com.gasguru.core.testing"
}

kotlin {
    sourceSets {
        commonMain.dependencies {
            api(projects.core.analytics)
            api(projects.core.data)
            api(projects.core.database)
            api(projects.core.model)
            api(projects.core.supabase)
            api(projects.navigation)
            api(libs.io.arrow.kt.arrow.core)
            api(libs.kotlinx.coroutines.test)
        }
        androidMain.dependencies {
            api(projects.core.network)
            api(libs.junit)
            api(libs.androidx.test.core)
            api(libs.androidx.test.espresso.core)
            api(libs.androidx.test.runner)
            api(libs.androidx.test.rules)
            api(libs.koin.test.junit5)
            api(libs.bundles.testing)
            api(libs.androidx.compose.ui.test.manifest)
            api(libs.androidx.compose.ui.tooling)
            api(libs.junit5.compose)
            api(libs.junit5.api)
            api(libs.junit5.extensions)
            api(libs.junit5.runner)
        }
        commonTest.dependencies {
            implementation(kotlin("test"))
        }
    }
}

// L001: garantizar runtime classpath para consumers Android
dependencies {
    api(libs.io.arrow.kt.arrow.core)
    api(libs.kotlinx.coroutines.test)
    api(libs.bundles.testing)
    api(libs.junit)
}
