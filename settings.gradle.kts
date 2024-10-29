pluginManagement {
    repositories {
        gradlePluginPortal()
        google()
        mavenCentral()
    }
}

@Suppress("UnstableApiUsage")
dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
    }
}

rootProject.name = "GasGuru"

include(":app")

include(":core:database")
include(":core:testing")
include(":core:ui")
include(":core:data")
include(":core:domain")
include(":core:network")
include(":core:model")
include(":core:uikit")
include(":core:common")

include(":feature:onboarding")
include(":feature:detail-station")
include(":feature:favorite-list-station")
include(":feature:station-map")
include(":feature:profile")
