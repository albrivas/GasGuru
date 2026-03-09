package com.gasguru.core.ui.models

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import com.gasguru.core.model.data.VehicleType
import com.gasguru.core.ui.R
import com.gasguru.core.uikit.R as RUikit

data class VehicleTypeUiModel(
    val type: VehicleType,
    @StringRes val nameRes: Int,
    @DrawableRes val iconRes: Int,
) {
    companion object {
        val ALL_TYPES = listOf(
            VehicleTypeUiModel(
                type = VehicleType.CAR,
                nameRes = R.string.vehicle_type_car,
                iconRes = RUikit.drawable.ic_vehicle_car,
            ),
            VehicleTypeUiModel(
                type = VehicleType.MOTORCYCLE,
                nameRes = R.string.vehicle_type_motorcycle,
                iconRes = RUikit.drawable.ic_vehicle_motorcycle,
            ),
            VehicleTypeUiModel(
                type = VehicleType.VAN,
                nameRes = R.string.vehicle_type_van,
                iconRes = RUikit.drawable.ic_vehicle_van,
            ),
            VehicleTypeUiModel(
                type = VehicleType.TRUCK,
                nameRes = R.string.vehicle_type_truck,
                iconRes = RUikit.drawable.ic_vehicle_truck,
            ),
        )
    }
}
