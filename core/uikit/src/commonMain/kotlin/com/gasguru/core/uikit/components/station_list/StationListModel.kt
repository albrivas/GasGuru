package com.gasguru.core.uikit.components.station_list

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector

data class StationListSwipeModel(
    val icon: ImageVector,
    val backgroundColor: Color,
    val onSwipe: (Int) -> Unit,
)
