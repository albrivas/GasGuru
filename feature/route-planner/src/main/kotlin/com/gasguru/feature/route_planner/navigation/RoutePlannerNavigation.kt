package com.gasguru.feature.route_planner.navigation

import androidx.compose.ui.window.DialogProperties
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.compose.dialog
import com.gasguru.core.ui.ConfigureDialogSystemBars
import com.gasguru.feature.route_planner.ui.RoutePlannerScreenRoute
import com.gasguru.navigation.constants.NavigationKeys
import com.gasguru.navigation.extensions.getPreviousResult
import com.gasguru.navigation.extensions.removePreviousResult
import com.gasguru.navigation.models.PlaceArgs

fun NavController.navigateToRoutePlannerScreen(navOptions: NavOptions? = null) {
    navigate(RoutePlannerRoute, navOptions)
}

fun NavGraphBuilder.routePlannerScreen() {
    dialog<RoutePlannerRoute>(
        dialogProperties = DialogProperties(
            usePlatformDefaultWidth = false,
            decorFitsSystemWindows = false
        )
    ) { navBackResult ->
        val result = navBackResult.getPreviousResult<PlaceArgs?>(NavigationKeys.SELECTED_PLACE)
        if (result != null) {
            navBackResult.removePreviousResult(NavigationKeys.SELECTED_PLACE)
        }

        ConfigureDialogSystemBars()

        RoutePlannerScreenRoute(selectedPlaceId = result)
    }
}
