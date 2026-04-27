package com.gasguru.core.uikit.components.swipe

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector

data class SwipeItemModel(
    val icon: ImageVector,
    val backgroundColor: Color,
    val onClick: () -> Unit,
    val enableDismissFromEndToStart: Boolean = true,
    val enableDismissFromStartToEnd: Boolean = true,
)
