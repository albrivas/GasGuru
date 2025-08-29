package com.gasguru.navigation.graphs

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.navigation
import com.gasguru.core.model.data.SearchPlace
import com.gasguru.feature.route_planner.navigation.RoutePlannerRoute
import com.gasguru.feature.route_planner.navigation.routePlannerScreen
import com.gasguru.feature.search.navigation.searchScreen
import com.gasguru.navigation.models.RoutePlanArgs
import kotlinx.serialization.Serializable

@Serializable
data object RouteSearchGraph

fun NavController.navigateToRouteSearchGraph(navOptions: NavOptions? = null) {
    navigate(RouteSearchGraph, navOptions)
}

fun NavGraphBuilder.routeSearchGraph(
    onBack: () -> Unit = {},
    navigateToSearch: () -> Unit = {},
    popBackToRoutePlanner: (SearchPlace) -> Unit = {},
    popBackToMapScreen: (RoutePlanArgs) -> Unit = {},
) {
    navigation<RouteSearchGraph>(
        startDestination = RoutePlannerRoute
    ) {
        routePlannerScreen(
            onBack = onBack,
            navigateToSearch = navigateToSearch,
            popBackToMapScreen = popBackToMapScreen
        )

        searchScreen(
            onPlaceSelected = { place: SearchPlace ->
                popBackToRoutePlanner(place)
            },
            onBackPressed = onBack
        )
    }
}
