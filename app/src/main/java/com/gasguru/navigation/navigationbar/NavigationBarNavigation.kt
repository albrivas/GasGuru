package com.gasguru.navigation.navigationbar

import androidx.compose.animation.slideInVertically
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.compose.composable
import com.gasguru.navigation.constants.NavigationKeys
import com.gasguru.navigation.extensions.getResultStateFlow
import com.gasguru.navigation.models.RoutePlanArgs
import com.gasguru.navigation.navigationbar.route.NavigationBarRoute
import com.gasguru.ui.NavigationBarScreenRoute

fun NavController.navigateToNavigationBar(navOptions: NavOptions? = null) {
    navigate(NavigationBarRoute, navOptions)
}

internal fun NavGraphBuilder.navigationBarHost() {
    composable<NavigationBarRoute>(
        enterTransition = { slideInVertically { it } },
    ) { navBackStackEntry ->
        val routePlanArgs by navBackStackEntry
            .getResultStateFlow<RoutePlanArgs>(key = NavigationKeys.ROUTE_PLANNER)
            .collectAsStateWithLifecycle(initialValue = null)

        NavigationBarScreenRoute(
            routePlanner = routePlanArgs,
            onRoutePlanConsumed = {
                navBackStackEntry.savedStateHandle[NavigationKeys.ROUTE_PLANNER] = null
            },
        )
    }
}