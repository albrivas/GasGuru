package com.gasguru.core.ui.mapper

import com.gasguru.core.model.data.FuelStation
import com.gasguru.core.ui.models.FuelStationUiModel

/**
 * Maps [FuelStation] domain model to [FuelStationUiModel].
 *
 * @receiver FuelStation domain model
 * @return FuelStationUiModel UI representation
 */
fun FuelStation.toUiModel(): FuelStationUiModel = FuelStationUiModel(
    fuelStation = this,
    brandIcon = brandStationBrandsType.toUiModel().iconRes,
    formattedDistance = formatDistance(),
    formattedDirection = formatDirection(),
    formattedName = formatName(),
)
