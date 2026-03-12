package com.gasguru.core.uikit.components.vehicle_item

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes

data class VehicleItemCardModel(
    val id: Long,
    val name: String?,
    @DrawableRes val vehicleTypeIconRes: Int,
    @StringRes val fuelTypeTranslationRes: Int,
    val tankCapacityLitres: Int,
    val isSelected: Boolean,
)
