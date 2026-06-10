package com.gasguru.feature.vehicle.navigation

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.compose.dialog
import com.gasguru.core.ui.ConfigureDialogSystemBars
import com.gasguru.core.ui.fullScreenDialogProperties
import com.gasguru.feature.vehicle.ui.AddVehicleRoute
import kotlinx.serialization.Serializable

fun NavController.navigateToAddVehicle(vehicleId: Long? = null, navOptions: NavOptions? = null) {
    this.navigate(VehicleRoutes.AddVehicleRoute(vehicleId = vehicleId), navOptions)
}

fun NavGraphBuilder.addVehicleScreen() {
    dialog<VehicleRoutes.AddVehicleRoute>(
        dialogProperties = fullScreenDialogProperties(),
    ) {
        ConfigureDialogSystemBars(invertColors = true)
        AddVehicleRoute()
    }
}

@Serializable
sealed class VehicleRoutes {
    @Serializable
    data class AddVehicleRoute(val vehicleId: Long? = null) : VehicleRoutes()
}
