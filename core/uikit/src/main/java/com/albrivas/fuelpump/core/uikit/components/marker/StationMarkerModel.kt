package com.albrivas.fuelpump.core.uikit.components.marker

import androidx.compose.ui.graphics.Color

data class StationMarkerModel(
    val icon: Int,
    val price: String,
    val color: Color,
    val isSelected: Boolean,
)
