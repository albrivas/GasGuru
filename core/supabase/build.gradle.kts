import com.codingfeline.buildkonfig.compiler.FieldSpec
import java.util.Properties

plugins {
    alias(libs.plugins.gasguru.kmp.library)
    alias(libs.plugins.gasguru.koin)
    alias(libs.plugins.gasguru.proguard)
    alias(libs.plugins.kotlin.serialization)
    alias(libs.plugins.buildkonfig)
}

android {
    namespace = "com.gasguru.core.supabase"
}

val localProps = Properties().apply {
    rootProject.file("local.properties").takeIf { it.exists() }
        ?.inputStream()
        ?.use { load(it) }
}

buildkonfig {
    packageName = "com.gasguru.core.supabase"
    objectName = "SupabaseSecrets"

    defaultConfigs {
        val supabaseUrl = localProps.getProperty("supabaseUrl")
            ?: System.getenv("SUPABASE_URL").orEmpty()
        val supabaseKey = localProps.getProperty("supabaseKey")
            ?: System.getenv("SUPABASE_KEY").orEmpty()

        buildConfigField(FieldSpec.Type.STRING, "SUPABASE_URL", supabaseUrl)
        buildConfigField(FieldSpec.Type.STRING, "SUPABASE_KEY", supabaseKey)
    }
}

kotlin {
    sourceSets {
        commonMain {
            kotlin.srcDir(tasks.named("generateBuildKonfig"))
            dependencies {
                implementation(libs.supabase.postgrest)
                implementation(libs.ktor.client.core)
                implementation(libs.kotlinx.coroutines.core)
                implementation(libs.kotlinx.serialization.json)
                implementation(libs.io.arrow.kt.arrow.core)
                implementation(projects.core.analytics)
            }
        }
        androidMain.dependencies {
            implementation(libs.ktor.client.okhttp)
            implementation(libs.kotlinx.coroutines.android)
        }
        iosMain.dependencies {
            implementation(libs.ktor.client.darwin)
        }
        commonTest.dependencies {
            implementation(kotlin("test"))
            implementation(libs.ktor.client.mock)
            implementation(libs.kotlinx.coroutines.test)
        }
    }
}

