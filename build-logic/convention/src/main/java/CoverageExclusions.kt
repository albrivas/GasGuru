object CoverageExclusions {
    val excludedModules = setOf(
        ":core:testing",
        ":core:uikit",
        ":core:ui",
        ":core:notifications",
        ":navigation",
        ":mocknetwork",
    )

    val excludedFilePatterns = listOf(
        "**/di/**",
        "**/analytics/**",
        "**/*AnalyticsHelper*.*",
        "**/theme/**",
        "**/*Widget*.*",
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
        "**/*_Impl*.*",
        "**/*JsonAdapter*.*",
        "**/*MapperImpl*.*",
        "**/*ComposableSingletons*.*",
        "**/*Preview*.*",
        "**/*\$*\$*.*",
        "**/*\$DefaultImpls.*",
        "**/*UiState*.*",
        "**/*UiModel*.*",
        "**/*Screen*.*",
        "**/*State.*",
        "**/*Event*.*",
        "**/*Page.*",
        "**/*App.*",
        "**/*Preferences.*",
        "**/res/**",
        "**/generated/resources/**",
        "**/*ConnectivityManagerNetworkMonitor*",
        "**/*LocationTrackerRepository*",
        "**/*GeocoderAddressImpl*",
        "**/*PlacesRepositoryImp*",
        "**/*RoutesRepositoryImpl*",
        // Compose UI composables: coverage only measurable via device tests (connectedAndroidTest),
        // not via JVM unit tests. Excluded to avoid artificially deflating the metric.
        "**/GasGuruSearchBarKt.*",  // JaCoCo: compiled class exclusion
        "**/GasGuruSearchBar.kt",   // SonarCloud: source file exclusion
        // Platform-specific Composable expect/actual implementations (androidMain / iosMain):
        // these require Android context or iOS runtime and cannot be covered by JVM unit tests.
        "**/platform/**",
    )

    /**
     * Converts module paths and file patterns to SonarCloud format
     */
    val sonarCoverageExclusions: String
        get() = (excludedModules.map { it.replace(":", "/").removePrefix("/") + "/**" } + excludedFilePatterns)
            .joinToString(",")
}