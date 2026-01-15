package com.gasguru.navigation.manager

import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Implementation of [NavigationManager].
 * Emits navigation events through a SharedFlow that will be collected by the NavHost.
 */
@Singleton
class NavigationManagerImpl @Inject constructor() : NavigationManager {

    private val _navigationFlow = MutableSharedFlow<NavigationDestination>(
        extraBufferCapacity = 10,
        onBufferOverflow = BufferOverflow.DROP_OLDEST,
    )

    override val navigationFlow: SharedFlow<NavigationDestination> = _navigationFlow.asSharedFlow()

    override fun navigateTo(destination: NavigationDestination) {
        _navigationFlow.tryEmit(value = destination)
    }

    override fun navigateBack() {
        _navigationFlow.tryEmit(value = NavigationDestination.Back)
    }
}