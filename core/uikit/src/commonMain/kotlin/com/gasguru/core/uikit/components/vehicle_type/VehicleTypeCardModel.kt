package com.gasguru.core.uikit.components.vehicle_type

import org.jetbrains.compose.resources.DrawableResource

data class VehicleTypeCardModel(
    val iconRes: DrawableResource,
    val nameRes: String,
    val isSelected: Boolean,
    val onClick: () -> Unit,
)
