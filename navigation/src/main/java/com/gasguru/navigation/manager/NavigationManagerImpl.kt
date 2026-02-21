package com.gasguru.navigation.manager

import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow

/**
 * Implementation of [NavigationManager].
 * Emits navigation commands through a SharedFlow that will be collected by the NavHost.
 */
class NavigationManagerImpl : NavigationManager {

    private val _navigationFlow = MutableSharedFlow<NavigationCommand>(
        extraBufferCapacity = 10,
        onBufferOverflow = BufferOverflow.DROP_OLDEST,
    )

    override val navigationFlow: SharedFlow<NavigationCommand> = _navigationFlow.asSharedFlow()

    override fun navigateTo(destination: NavigationDestination) {
        _navigationFlow.tryEmit(value = NavigationCommand.To(destination = destination))
    }

    override fun navigateBack() {
        _navigationFlow.tryEmit(value = NavigationCommand.Back)
    }

    override fun navigateBackTo(route: Any, inclusive: Boolean) {
        _navigationFlow.tryEmit(
            value = NavigationCommand.BackTo(route = route, inclusive = inclusive)
        )
    }

    override fun navigateBackWithData(key: String, value: Any) {
        _navigationFlow.tryEmit(
            value = NavigationCommand.BackWithData(key = key, value = value)
        )
    }
}
