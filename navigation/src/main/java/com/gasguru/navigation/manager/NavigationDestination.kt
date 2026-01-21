package com.gasguru.navigation.manager

/**
 * Sealed interface representing all possible navigation destinations in the app.
 * Centralizes all navigation paths for better maintainability and type-safety.
 */
sealed interface NavigationDestination {

    /**
     * Navigate to station detail screen.
     *
     * @param idServiceStation The ID of the station to display
     * @param presentAsDialog Whether to present as a dialog or full screen
     */
    data class DetailStation(
        val idServiceStation: Int,
        val presentAsDialog: Boolean = false,
    ) : NavigationDestination

    /**
     * Navigate to onboarding welcome screen.
     */
    data object OnboardingWelcome : NavigationDestination

    /**
     * Navigate to onboarding fuel preferences screen.
     */
    data object OnboardingFuelPreferences : NavigationDestination

    /**
     * Navigate to home screen (navigation bar).
     * This typically clears the back stack.
     */
    data object Home : NavigationDestination

    /**
     * Navigate to search screen.
     */
    data object Search : NavigationDestination

    /**
     * Navigate to route planner screen.
     */
    data object RoutePlanner : NavigationDestination
}
