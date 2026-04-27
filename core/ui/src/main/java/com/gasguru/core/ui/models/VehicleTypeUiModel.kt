package com.gasguru.core.ui.models

import androidx.annotation.StringRes
import com.gasguru.core.model.data.VehicleType
import com.gasguru.core.ui.R
import com.gasguru.core.uikit.components.icon.VehicleTypeIcons
import org.jetbrains.compose.resources.DrawableResource

data class VehicleTypeUiModel(
    val type: VehicleType,
    @StringRes val nameRes: Int,
    val iconRes: DrawableResource,
) {
    companion object {
        val ALL_TYPES = listOf(
            VehicleTypeUiModel(
                type = VehicleType.CAR,
                nameRes = R.string.vehicle_type_car,
                iconRes = VehicleTypeIcons.Car,
            ),
            VehicleTypeUiModel(
                type = VehicleType.MOTORCYCLE,
                nameRes = R.string.vehicle_type_motorcycle,
                iconRes = VehicleTypeIcons.Motorcycle,
            ),
            VehicleTypeUiModel(
                type = VehicleType.VAN,
                nameRes = R.string.vehicle_type_van,
                iconRes = VehicleTypeIcons.Van,
            ),
            VehicleTypeUiModel(
                type = VehicleType.TRUCK,
                nameRes = R.string.vehicle_type_truck,
                iconRes = VehicleTypeIcons.Truck,
            ),
        )
    }
}
