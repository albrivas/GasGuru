package com.gasguru.core.uikit.components.station_list

import androidx.compose.ui.graphics.Color

data class StationListSwipeModel(
    val iconAnimated: Int,
    val backgroundColor: Color,
    val onSwipe: (Int) -> Unit
)
