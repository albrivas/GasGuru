package com.gasguru.feature.station_map.navigation

import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.gasguru.feature.station_map.navigation.route.StationMapGraph
import com.gasguru.feature.station_map.ui.StationMapScreenRoute

fun NavGraphBuilder.stationMapScreen(
    navigateToDetail: (Int) -> Unit,
    navigateToRoutePlanner: () -> Unit = {},
) {
    composable<StationMapGraph.StationMapRoute>(
        enterTransition = { null },
        exitTransition = { null },
        popEnterTransition = { null },
        popExitTransition = { null }
    ) {
        StationMapScreenRoute(
            routePlanner = null,
            navigateToDetail = navigateToDetail,
            navigateToRoutePlanner = navigateToRoutePlanner
        )
    }
}
