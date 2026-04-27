package com.gasguru.core.uikit.components.fuelItem

import androidx.compose.ui.graphics.Color
import org.jetbrains.compose.resources.DrawableResource

data class FuelStationItemModel(
    val idServiceStation: Int,
    val icon: DrawableResource,
    val name: String,
    val distance: String,
    val price: String,
    val index: Int,
    val categoryColor: Color,
    val onItemClick: (Int) -> Unit,
)
