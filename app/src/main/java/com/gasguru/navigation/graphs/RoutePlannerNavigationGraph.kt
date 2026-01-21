package com.gasguru.navigation.graphs

import androidx.navigation.NavGraphBuilder
import androidx.navigation.navigation
import com.gasguru.feature.route_planner.navigation.RoutePlannerRoute
import com.gasguru.feature.route_planner.navigation.routePlannerScreen
import com.gasguru.feature.search.navigation.searchScreen
import kotlinx.serialization.Serializable

@Serializable
data object RouteSearchGraph

fun NavGraphBuilder.routeSearchGraph() {
    navigation<RouteSearchGraph>(
        startDestination = RoutePlannerRoute
    ) {
        routePlannerScreen()
        searchScreen()
    }
}
