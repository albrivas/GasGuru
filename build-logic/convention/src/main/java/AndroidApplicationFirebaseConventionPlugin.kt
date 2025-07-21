import com.gasguru.build_logic.convention.getLibrary
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.apply
import org.gradle.kotlin.dsl.dependencies

class AndroidApplicationFirebaseConventionPlugin : Plugin<Project> {
    override fun apply(target: Project) {
        with(target) {
            apply(plugin = "com.google.firebase.crashlytics")

            dependencies {
                add("implementation", getLibrary("firebase.crashlytics"))
            }
        }
    }

}
