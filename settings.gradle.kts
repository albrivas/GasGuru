pluginManagement {
    repositories {
        gradlePluginPortal()
        google()
        mavenCentral()
    }
}
dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
    }
}
rootProject.name = "FuelPump"

include(":app")
include(":feature:home")
include(":feature:splash")
include(":core:database")
include(":core:testing")
include(":core:ui")
include(":core:data")
include(":core:domain")
include(":core:network")
include(":core:model")
include(":core:uikit")
