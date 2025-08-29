package com.gasguru.core.uikit.components.station_list

import androidx.compose.ui.graphics.Color

data class StationListItemModel(
    val idServiceStation: Int,
    val icon: Int,
    val name: String,
    val distance: String,
    val price: String,
    val categoryColor: Color
)