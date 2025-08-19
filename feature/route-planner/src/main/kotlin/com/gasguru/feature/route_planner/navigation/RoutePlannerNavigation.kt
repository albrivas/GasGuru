package com.gasguru.feature.route_planner.navigation

import androidx.compose.ui.window.DialogProperties
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.compose.dialog
import com.gasguru.feature.route_planner.ui.RoutePlannerScreenRoute

fun NavController.navigateToRoutePlannerScreen(navOptions: NavOptions? = null) {
    navigate(RoutePlannerRoute, navOptions)
}

fun NavGraphBuilder.routePlannerScreen(
    onBack: () -> Unit = {},
    navigateToSearch: () -> Unit = {},
) {
    dialog<RoutePlannerRoute>(
        dialogProperties = DialogProperties(
            usePlatformDefaultWidth = false,
            decorFitsSystemWindows = false
        )
    ) { navBackResult ->
        val selectedPlace = navBackResult.savedStateHandle.get<Pair<String, String>?>("selected_place")
        if (selectedPlace != null) {
            navBackResult.savedStateHandle.remove<String>("selected_place")
        }
        RoutePlannerScreenRoute(
            selectedPlaceId = selectedPlace,
            onBack = onBack,
            navigateToSearch = navigateToSearch,
        )
    }
}
