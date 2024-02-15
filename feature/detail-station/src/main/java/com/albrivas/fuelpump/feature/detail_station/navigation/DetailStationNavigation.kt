package com.albrivas.fuelpump.feature.detail_station.navigation

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.compose.composable
import com.albrivas.fuelpump.core.model.data.FuelStation
import com.albrivas.fuelpump.feature.detail_station.ui.DetailStationScreenRoute

const val nameArgument = "idStation"
const val fuelStationDetailRoute = "detail_station"

fun NavController.navigateToFuelStationDetail(fuelStationId: String, navOptions: NavOptions? = null, ) {
    this.navigate("$fuelStationDetailRoute/{$fuelStationId}", navOptions)
}

fun NavGraphBuilder.detailStationScreen() {
    composable(route = "$fuelStationDetailRoute/{$nameArgument}") {
        val idStation = it.arguments?.getString(nameArgument)
        DetailStationScreenRoute(idStation = idStation)
    }
}
