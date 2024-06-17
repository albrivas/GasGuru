package com.albrivas.fuelpump.core.uikit.shape

import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Outline
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.LayoutDirection

class CircleArrowShape: Shape {
    override fun createOutline(
        size: Size,
        layoutDirection: LayoutDirection,
        density: Density
    ): Outline {
        return Outline.Generic(Path().apply {
            val radius = size.width / 5
            val arrowHeight = size.height / 5
            val arrowWidth = size.width / 8

            // Comienza en la esquina superior izquierda
            moveTo(x = 0f, y = radius)

            // Dibuja la esquina superior izquierda del rectángulo redondeado
            arcTo(
                rect = Rect(left = 0f, top = 0f, right = 2 * radius, bottom = 2 * radius),
                startAngleDegrees = 180f,
                sweepAngleDegrees = 90f,
                forceMoveTo = false
            )

            // Dibuja el borde superior del rectángulo redondeado
            lineTo(x = size.width - radius, y = 0f)

            // Dibuja la esquina superior derecha del rectángulo redondeado
            arcTo(
                rect = Rect(size.width - 2 * radius, 0f, size.width, 2 * radius),
                startAngleDegrees = -90f,
                sweepAngleDegrees = 90f,
                forceMoveTo = false
            )

            // Dibuja el borde derecho del rectángulo redondeado
            lineTo(x = size.width, y = size.height - arrowHeight - radius)

            // Dibuja la esquina inferior derecha del rectángulo redondeado
            arcTo(
                rect = Rect(
                    size.width - 2 * radius,
                    size.height - arrowHeight - 2 * radius,
                    size.width,
                    size.height - arrowHeight
                ),
                startAngleDegrees = 0f,
                sweepAngleDegrees = 90f,
                forceMoveTo = false
            )

            // Dibuja la flecha en la parte inferior
            lineTo(x = size.width / 2 + arrowWidth / 2, y = size.height - arrowHeight)
            lineTo(x = size.width / 2, y = size.height)
            lineTo(x = size.width / 2 - arrowWidth / 2, y = size.height - arrowHeight)

            // Dibuja la esquina inferior izquierda del rectángulo redondeado
            lineTo(x = radius, y = size.height - arrowHeight)
            arcTo(
                rect = Rect(
                    0f,
                    size.height - arrowHeight - 2 * radius,
                    2 * radius,
                    size.height - arrowHeight
                ),
                startAngleDegrees = 90f,
                sweepAngleDegrees = 90f,
                forceMoveTo = false
            )

            close()
        })
    }
}