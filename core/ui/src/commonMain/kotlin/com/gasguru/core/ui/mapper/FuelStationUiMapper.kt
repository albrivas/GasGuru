package com.gasguru.core.ui.mapper

import com.gasguru.core.model.data.FuelStation
import com.gasguru.core.ui.models.FuelStationUiModel

fun FuelStation.toUiModel(): FuelStationUiModel = FuelStationUiModel(
    fuelStation = this,
    brandIcon = brandStationBrandsType.toUiModel().iconRes,
    formattedDistance = formatDistance(),
    formattedDirection = formatDirection(),
    formattedName = formatName(),
)
