object CoverageExclusions {
    val excludedModules = setOf(
        ":core:testing",
        ":core:uikit",
        ":core:ui",
        ":navigation",
        ":mocknetwork",
    )

    val excludedFilePatterns = listOf(
        "**/di/**",
        "**/BuildConfig.*",
        "**/R.class",
        "**/R\\$*.class",
        "**/*Test*.*",
        "**/model/**",
        "**/mapper/**",
        "**/navigation/**",
        "**/Manifest*.*",
        "**/*Activity.*",
        "**/*Application.*",
        "**/*.gradle.kts",
        "**/*_Factory.*",
        "**/*_MembersInjector.*",
        "**/*_HiltModules*.*",
        "**/Hilt_*.*",
        "**/*Hilt*.*",
        "**/*Dagger*.*",
        "**/*AssistedFactory*.*",
        "**/*AssistedInject*.*",
        "**/*_Impl*.*",
        "**/*JsonAdapter*.*",
        "**/*MapperImpl*.*",
        "**/*ComposableSingletons*.*",
        "**/*Preview*.*",
        "**/*\$*\$*.*",
        "**/*UiState*.*",
        "**/*UiModel*.*",
        "**/*Screen*.*",
        "**/*State.*",
        "**/*Event*.*",
        "**/*Page.*",
        "**/*App.*",
        "**/*Preferences.*",
        "**/res/**",
    )

    /**
     * Converts module paths and file patterns to SonarCloud format
     */
    val sonarCoverageExclusions: String
        get() = (excludedModules.map { it.replace(":", "/").removePrefix("/") + "/**" } + excludedFilePatterns)
            .joinToString(",")
}