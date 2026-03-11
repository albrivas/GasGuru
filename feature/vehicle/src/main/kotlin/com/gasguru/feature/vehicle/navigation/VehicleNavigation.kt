package com.gasguru.feature.vehicle.navigation

import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.compose.composable
import com.gasguru.feature.vehicle.ui.AddVehicleRoute
import kotlinx.serialization.Serializable

fun NavController.navigateToAddVehicle(navOptions: NavOptions? = null) {
    this.navigate(VehicleRoutes.AddVehicleRoute, navOptions)
}

fun NavGraphBuilder.addVehicleScreen() {
    composable<VehicleRoutes.AddVehicleRoute>(
        enterTransition = { slideInHorizontally { it } },
        popExitTransition = { slideOutHorizontally { it } },
    ) {
        AddVehicleRoute()
    }
}

@Serializable
sealed class VehicleRoutes {
    @Serializable
    data object AddVehicleRoute : VehicleRoutes()
}
