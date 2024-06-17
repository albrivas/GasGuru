package com.albrivas.feature.station_map.navigation

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.compose.composable
import com.albrivas.feature.station_map.ui.StationMapScreenRoute

fun NavController.navigateToStationMap(navOptions: NavOptions? = null) {
    this.navigate(StationMapRoute, navOptions)
}

fun NavGraphBuilder.stationMapScreen() {
    composable<StationMapRoute> {
        StationMapScreenRoute()
    }
}