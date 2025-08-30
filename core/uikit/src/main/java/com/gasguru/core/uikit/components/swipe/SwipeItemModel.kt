package com.gasguru.core.uikit.components.swipe

import androidx.compose.ui.graphics.Color

data class SwipeItemModel(
    val iconAnimated: Int,
    val backgroundColor: Color,
    val onClick: () -> Unit,
    val enableDismissFromEndToStart: Boolean = true,
    val enableDismissFromStartToEnd: Boolean = true
)
