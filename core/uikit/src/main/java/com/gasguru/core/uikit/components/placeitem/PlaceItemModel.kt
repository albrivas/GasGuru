package com.gasguru.core.uikit.components.placeitem

import androidx.compose.ui.graphics.vector.ImageVector

data class PlaceItemModel(
    val id: String,
    val icon: ImageVector,
    val name: String,
    val onClickItem: () -> Unit,
)
