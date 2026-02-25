
import androidx.room.gradle.RoomExtension
import com.gasguru.build_logic.convention.getLibrary
import com.google.devtools.ksp.gradle.KspExtension
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.configure
import org.gradle.kotlin.dsl.dependencies
import org.jetbrains.kotlin.gradle.dsl.KotlinMultiplatformExtension

class KmpRoomConventionPlugin : Plugin<Project> {
    override fun apply(target: Project) {
        with(target) {
            pluginManager.apply("androidx.room")
            pluginManager.apply("com.google.devtools.ksp")

            extensions.configure<KspExtension> {
                arg("room.generateKotlin", "true")
            }

            extensions.configure<RoomExtension> {
                schemaDirectory("$projectDir/schemas")
            }

            extensions.configure<KotlinMultiplatformExtension> {
                sourceSets.commonMain.dependencies {
                    implementation(getLibrary("androidx.room.runtime"))
                }
            }

            dependencies {
                add("kspAndroid", getLibrary("androidx.room.compiler"))
                add("kspIosX64", getLibrary("androidx.room.compiler"))
                add("kspIosArm64", getLibrary("androidx.room.compiler"))
                add("kspIosSimulatorArm64", getLibrary("androidx.room.compiler"))
            }
        }
    }
}