package com.albrivas.fuelpump.feature.fuel_list_station.navigation

import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.albrivas.fuelpump.feature.fuel_list_station.navigation.route.StationListGraph
import com.albrivas.fuelpump.feature.fuel_list_station.ui.FuelStationListScreenRoute

fun NavGraphBuilder.fuelStationListScreen(navigateToDetail: (Int) -> Unit) {
    composable<StationListGraph.StationListRoute> {
        FuelStationListScreenRoute(navigateToDetail = navigateToDetail)
    }
}
