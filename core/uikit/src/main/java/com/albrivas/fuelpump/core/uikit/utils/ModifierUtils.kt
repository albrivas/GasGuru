package com.albrivas.fuelpump.core.uikit.utils

import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color

fun Modifier.borderWithoutTopBorder(strokeWidth: Float = 3f, color: Color = Color.Black): Modifier =
    this.drawBehind {
        val bottomY = size.height - strokeWidth / 2
        val rightX = size.width - strokeWidth / 2

        // Dibuja la línea inferior
        drawLine(
            color = color,
            start = Offset(0f, bottomY),
            end = Offset(size.width, bottomY),
            strokeWidth = strokeWidth
        )

        // Dibuja la línea izquierda
        drawLine(
            color = color,
            start = Offset(strokeWidth / 2, 0f),
            end = Offset(strokeWidth / 2, size.height),
            strokeWidth = strokeWidth
        )

        // Dibuja la línea derecha
        drawLine(
            color = color,
            start = Offset(rightX, 0f),
            end = Offset(rightX, size.height),
            strokeWidth = strokeWidth
        )
    }