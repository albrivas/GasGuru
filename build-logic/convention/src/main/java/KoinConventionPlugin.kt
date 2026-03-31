
import com.gasguru.build_logic.convention.getLibrary
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.configure
import org.gradle.kotlin.dsl.dependencies
import org.jetbrains.kotlin.gradle.dsl.KotlinMultiplatformExtension

class KoinConventionPlugin : Plugin<Project> {
    override fun apply(target: Project) {
        with(target) {
            // KMP modules: koin-core to commonMain, koin-android to androidMain
            pluginManager.withPlugin("org.jetbrains.kotlin.multiplatform") {
                extensions.configure<KotlinMultiplatformExtension> {
                    sourceSets.commonMain.dependencies {
                        implementation(getLibrary("koin.core"))
                    }
                    sourceSets.androidMain.dependencies {
                        implementation(getLibrary("koin.android"))
                    }
                }
            }
            // Pure Android modules: koin-android the normal way
            pluginManager.withPlugin("com.android.base") {
                if (!pluginManager.hasPlugin("org.jetbrains.kotlin.multiplatform")) {
                    dependencies {
                        add("implementation", getLibrary("koin.android"))
                    }
                }
            }
        }
    }
}