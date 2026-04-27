package com.gasguru.core.uikit.components.marker

import androidx.compose.runtime.Stable
import androidx.compose.ui.graphics.Color
import org.jetbrains.compose.resources.DrawableResource

@Stable
data class StationMarkerModel(
    val icon: DrawableResource,
    val price: String,
    val color: Color,
    val isSelected: Boolean,
)
