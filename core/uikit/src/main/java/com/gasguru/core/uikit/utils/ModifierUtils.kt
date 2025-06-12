package com.gasguru.core.uikit.utils

import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.gasguru.core.uikit.theme.Neutral300

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

fun Modifier.drawBottomDivider(
    color: Color = Neutral300,
    thickness: Dp = 1.dp
): Modifier = this.then(
    Modifier.drawBehind {
        val y = size.height - thickness.toPx()
        drawLine(
            color = color,
            start = Offset(x = 0f, y = y),
            end = Offset(x = size.width, y = y),
            strokeWidth = thickness.toPx()
        )
    }
)
