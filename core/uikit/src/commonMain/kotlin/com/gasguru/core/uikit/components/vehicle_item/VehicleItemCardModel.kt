package com.gasguru.core.uikit.components.vehicle_item

import org.jetbrains.compose.resources.DrawableResource
import org.jetbrains.compose.resources.StringResource

data class VehicleItemCardModel(
    val id: Long,
    val name: String?,
    val vehicleTypeIconRes: DrawableResource,
    val fuelTypeTranslationRes: StringResource,
    val tankCapacityLitres: Int,
    val isSelected: Boolean,
)
