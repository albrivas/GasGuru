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
    ) {
        RoutePlannerScreenRoute(onBack = onBack, navigateToSearch = navigateToSearch)
    }
}