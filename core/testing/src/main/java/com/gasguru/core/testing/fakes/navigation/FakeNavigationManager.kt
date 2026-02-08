package com.gasguru.core.testing.fakes.navigation

import com.gasguru.navigation.manager.NavigationCommand
import com.gasguru.navigation.manager.NavigationDestination
import com.gasguru.navigation.manager.NavigationManager
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow

class FakeNavigationManager : NavigationManager {

    private val _navigationFlow = MutableSharedFlow<NavigationCommand>()
    override val navigationFlow: SharedFlow<NavigationCommand> = _navigationFlow.asSharedFlow()

    val navigatedDestinations = mutableListOf<NavigationDestination>()
    var navigateBackCalled = false
        private set

    override fun navigateTo(destination: NavigationDestination) {
        navigatedDestinations.add(destination)
    }

    override fun navigateBack() {
        navigateBackCalled = true
    }

    override fun navigateBackTo(route: Any, inclusive: Boolean) {
        // No-op
    }

    override fun navigateBackWithData(key: String, value: Any) {
        // No-op
    }
}