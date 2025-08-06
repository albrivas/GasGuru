package com.gasguru.core.uikit.utils

import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

fun Modifier.borderWithoutTopBorder(strokeWidth: Float = 3f, color: Color = Color.Black): Modifier =
    this.drawBehind {
        val bottomY = size.height - strokeWidth / 2
        val rightX = size.width - strokeWidth / 2

        // Draw the bottom line
        drawLine(
            color = color,
            start = Offset(0f, bottomY),
            end = Offset(size.width, bottomY),
            strokeWidth = strokeWidth
        )

        // Draw the left line
        drawLine(
            color = color,
            start = Offset(strokeWidth / 2, 0f),
            end = Offset(strokeWidth / 2, size.height),
            strokeWidth = strokeWidth
        )

        // Draw the right line
        drawLine(
            color = color,
            start = Offset(rightX, 0f),
            end = Offset(rightX, size.height),
            strokeWidth = strokeWidth
        )
    }

fun Modifier.horizontalDivider(color: Color, isLastItem: Boolean): Modifier =
    this.drawBehind {
        if (!isLastItem) {
            val lineY = size.height - 1.dp.toPx()
            drawLine(
                color = color,
                start = Offset(0f, lineY),
                end = Offset(size.width, lineY),
                strokeWidth = 1.dp.toPx()
            )
        }
    }

