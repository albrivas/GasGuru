package com.gasguru.feature.profile.ui.mapper

import android.content.Context
import com.gasguru.core.model.data.Vehicle
import com.gasguru.core.ui.mapper.toUiModel
import com.gasguru.core.ui.models.VehicleTypeUiModel
import com.gasguru.core.uikit.components.vehicle_item.VehicleItemCardModel

fun Vehicle.toVehicleItemCardModel(context: Context, isSelected: Boolean): VehicleItemCardModel = VehicleItemCardModel(
    id = id,
    name = name,
    vehicleTypeIconRes = VehicleTypeUiModel.ALL_TYPES.first { it.type == vehicleType }.iconRes,
    fuelTypeTranslationRes = context.getString(fuelType.toUiModel().translationRes),
    tankCapacityLitres = tankCapacity,
    isSelected = isSelected,
)
