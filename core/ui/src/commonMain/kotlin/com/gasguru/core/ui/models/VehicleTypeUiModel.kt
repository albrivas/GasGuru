package com.gasguru.core.ui.models

import com.gasguru.core.model.data.VehicleType
import com.gasguru.core.ui.generated.resources.Res
import com.gasguru.core.ui.generated.resources.vehicle_type_car
import com.gasguru.core.ui.generated.resources.vehicle_type_motorcycle
import com.gasguru.core.ui.generated.resources.vehicle_type_truck
import com.gasguru.core.ui.generated.resources.vehicle_type_van
import com.gasguru.core.uikit.components.icon.VehicleTypeIcons
import org.jetbrains.compose.resources.DrawableResource
import org.jetbrains.compose.resources.StringResource

data class VehicleTypeUiModel(
    val type: VehicleType,
    val nameRes: StringResource,
    val iconRes: DrawableResource,
) {
    companion object {
        val ALL_TYPES = listOf(
            VehicleTypeUiModel(
                type = VehicleType.CAR,
                nameRes = Res.string.vehicle_type_car,
                iconRes = VehicleTypeIcons.Car,
            ),
            VehicleTypeUiModel(
                type = VehicleType.MOTORCYCLE,
                nameRes = Res.string.vehicle_type_motorcycle,
                iconRes = VehicleTypeIcons.Motorcycle,
            ),
            VehicleTypeUiModel(
                type = VehicleType.VAN,
                nameRes = Res.string.vehicle_type_van,
                iconRes = VehicleTypeIcons.Van,
            ),
            VehicleTypeUiModel(
                type = VehicleType.TRUCK,
                nameRes = Res.string.vehicle_type_truck,
                iconRes = VehicleTypeIcons.Truck,
            ),
        )
    }
}
