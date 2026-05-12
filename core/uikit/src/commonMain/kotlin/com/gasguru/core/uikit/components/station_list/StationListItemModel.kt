package com.gasguru.core.uikit.components.station_list

import androidx.compose.ui.graphics.Color
import org.jetbrains.compose.resources.DrawableResource

data class StationListItemModel(
    val idServiceStation: Int,
    val icon: DrawableResource,
    val name: String,
    val distance: String,
    val price: String,
    val categoryColor: Color,
)
