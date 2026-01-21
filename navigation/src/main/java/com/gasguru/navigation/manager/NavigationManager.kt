package com.gasguru.navigation.manager

import kotlinx.coroutines.flow.SharedFlow

/**
 * Central navigation manager for the app.
 * Provides a single point of entry for all navigation actions.
 *
 * ViewModels and Composables should use this interface to navigate throughout the app.
 */
interface NavigationManager {
    /**
     * Flow of navigation commands to be observed by NavHost.
     * The NavHost will collect this flow and execute navigation actions.
     */
    val navigationFlow: SharedFlow<NavigationCommand>

    /**
     * Navigate to a specific destination.
     *
     * @param destination The destination to navigate to
     */
    fun navigateTo(destination: NavigationDestination)

    /**
     * Navigate back (pop current screen from back stack).
     */
    fun navigateBack()

    /**
     * Navigate back to a specific route in the back stack.
     */
    fun navigateBackTo(route: Any, inclusive: Boolean = false)

    /**
     * Navigate back and pass data to the previous screen.
     */
    fun navigateBackWithData(key: String, value: Any)
}
