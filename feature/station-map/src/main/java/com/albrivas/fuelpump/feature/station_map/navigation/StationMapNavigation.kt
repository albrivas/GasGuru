package com.albrivas.fuelpump.feature.station_map.navigation

import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.albrivas.fuelpump.feature.station_map.navigation.route.StationMapGraph
import com.albrivas.fuelpump.feature.station_map.ui.StationMapScreenRoute

fun NavGraphBuilder.stationMapScreen(navigateToDetail: (Int) -> Unit) {
    composable<StationMapGraph.StationMapRoute>(
        enterTransition = { null },
        exitTransition = { null },
        popEnterTransition = { null },
        popExitTransition = { null }
    ) {
        StationMapScreenRoute(navigateToDetail = navigateToDetail)
    }
}
