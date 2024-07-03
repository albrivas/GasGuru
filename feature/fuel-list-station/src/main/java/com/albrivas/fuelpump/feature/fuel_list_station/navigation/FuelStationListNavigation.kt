package com.albrivas.fuelpump.feature.fuel_list_station.navigation

import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.albrivas.fuelpump.feature.fuel_list_station.ui.FuelStationListScreenRoute

fun NavGraphBuilder.fuelStationListScreen() {
    composable<FuelStationListRoute> {
        FuelStationListScreenRoute()
    }
}
