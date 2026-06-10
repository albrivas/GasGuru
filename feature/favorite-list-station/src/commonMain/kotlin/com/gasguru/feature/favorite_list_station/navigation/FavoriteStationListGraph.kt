package com.gasguru.feature.favorite_list_station.navigation

import androidx.navigation.NavGraphBuilder
import androidx.navigation.navigation
import com.gasguru.feature.favorite_list_station.navigation.route.StationListGraph

fun NavGraphBuilder.favoriteGraph() {
    navigation<StationListGraph.StationListGraphRoute>(
        startDestination = StationListGraph.StationListRoute,
    ) {
        favoriteListStationScreen()
    }
}
