package com.gasguru.core.uikit.components.alert

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector

data class GasGuruAlertDialogModel(
    val icon: ImageVector,
    val iconTint: Color,
    val iconBackgroundColor: Color,
    val title: String,
    val description: String,
    val primaryButtonText: String,
    val secondaryButtonText: String? = null,
)
