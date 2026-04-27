package com.gasguru.core.uikit.components.vehicle_item

import org.jetbrains.compose.resources.DrawableResource

data class VehicleItemCardModel(
    val id: Long,
    val name: String?,
    val vehicleTypeIconRes: DrawableResource,
    val fuelTypeTranslationRes: String,
    val tankCapacityLitres: Int,
    val isSelected: Boolean,
)
