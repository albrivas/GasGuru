package com.gasguru.navigation.manager

import androidx.savedstate.SavedState
import kotlinx.coroutines.flow.SharedFlow

interface NavigationManager {
    val navigationFlow: SharedFlow<NavigationCommand>

    fun navigateTo(destination: NavigationDestination)

    fun navigateBack()

    fun navigateBackTo(route: Any, inclusive: Boolean = false)

    fun navigateBackWithData(key: String, value: SavedState)
}