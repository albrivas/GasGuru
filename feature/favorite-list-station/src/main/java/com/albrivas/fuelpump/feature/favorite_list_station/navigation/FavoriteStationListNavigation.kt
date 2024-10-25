package com.albrivas.fuelpump.feature.favorite_list_station.navigation

import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.albrivas.fuelpump.feature.favorite_list_station.navigation.route.StationListGraph
import com.albrivas.fuelpump.feature.favorite_list_station.ui.FavoriteListStationScreenRoute

fun NavGraphBuilder.favoriteListStationScreen(navigateToDetail: (Int) -> Unit) {
    composable<StationListGraph.StationListRoute>(
        enterTransition = { null },
        exitTransition = { null },
        popEnterTransition = { null },
        popExitTransition = { null }
    ) {
        FavoriteListStationScreenRoute(navigateToDetail = navigateToDetail)
    }
}
