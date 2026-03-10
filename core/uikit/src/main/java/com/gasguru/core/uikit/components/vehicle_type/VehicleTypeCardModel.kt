package com.gasguru.core.uikit.components.vehicle_type

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes

data class VehicleTypeCardModel(
    @DrawableRes val iconRes: Int,
    @StringRes val nameRes: Int,
    val isSelected: Boolean,
    val onClick: () -> Unit,
)
