package com.gasguru.core.uikit.components.divider

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

data class GasGuruDividerModel(
    val color: Color,
    val length: DividerLength,
    val thickness: DividerThickness
)

enum class DividerLength {
    FULL, INSET
}

enum class DividerThickness(val value: Dp) {
    MEDIUM(0.5.dp), THICK(1.dp)
}
