package com.gasguru.feature.favorite_list_station.navigation

import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.gasguru.feature.favorite_list_station.navigation.route.StationListGraph
import com.gasguru.feature.favorite_list_station.ui.FavoriteListStationScreenRoute

fun NavGraphBuilder.favoriteListStationScreen() {
    composable<StationListGraph.StationListRoute>(
        enterTransition = { null },
        exitTransition = { null },
        popEnterTransition = { null },
        popExitTransition = { null }
    ) {
        FavoriteListStationScreenRoute()
    }
}
