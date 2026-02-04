package com.gasguru.core.ui.models

import androidx.annotation.DrawableRes
import androidx.compose.runtime.Stable
import com.gasguru.core.model.data.FuelStation

@Stable
data class FuelStationUiModel(
    val fuelStation: FuelStation,
    @DrawableRes val brandIcon: Int,
    val formattedDistance: String,
    val formattedDirection: String,
    val formattedName: String,
) {

    companion object {
        fun from(fuelStation: FuelStation): FuelStationUiModel = FuelStationUiModel(
            fuelStation = fuelStation,
            brandIcon = FuelStationBrandsUiModel.fromBrandType(brandType = fuelStation.brandStationBrandsType).iconRes,
            formattedDistance = fuelStation.formatDistance(),
            formattedDirection = fuelStation.formatDirection(),
            formattedName = fuelStation.formatName()
        )
    }
}
