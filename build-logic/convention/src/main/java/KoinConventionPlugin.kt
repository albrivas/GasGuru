
import com.gasguru.build_logic.convention.getLibrary
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.dependencies

class KoinConventionPlugin : Plugin<Project> {
    override fun apply(target: Project) {
        with(target) {
            pluginManager.withPlugin("com.android.base") {
                dependencies {
                    add("implementation", getLibrary("koin.android"))
                }
            }
        }
    }
}
