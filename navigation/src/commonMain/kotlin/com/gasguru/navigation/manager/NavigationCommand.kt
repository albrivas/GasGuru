package com.gasguru.navigation.manager

import androidx.savedstate.SavedState

/**
 * Navigation actions emitted by [NavigationManager].
 */
sealed interface NavigationCommand {
    data class To(val destination: NavigationDestination) : NavigationCommand
    data object Back : NavigationCommand
    data class BackTo(
        val route: Any,
        val inclusive: Boolean = false,
    ) : NavigationCommand
    data class BackWithData(
        val key: String,
        val value: SavedState,
    ) : NavigationCommand
}
