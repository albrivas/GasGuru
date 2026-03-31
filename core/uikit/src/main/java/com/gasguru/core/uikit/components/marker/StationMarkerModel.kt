package com.gasguru.core.uikit.components.marker

import androidx.compose.runtime.Stable
import androidx.compose.ui.graphics.Color

@Stable
data class StationMarkerModel(
    val icon: Int,
    val price: String,
    val color: Color,
    val isSelected: Boolean,
)
