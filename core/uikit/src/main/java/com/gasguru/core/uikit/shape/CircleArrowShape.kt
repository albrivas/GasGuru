package com.gasguru.core.uikit.shape

import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.RoundRect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Outline
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.LayoutDirection

class CircleArrowShape() : Shape {
    override fun createOutline(
        size: Size,
        layoutDirection: LayoutDirection,
        density: Density,
    ): Outline {
        return Outline.Generic(
            path = Path().apply {
                addRoundRect(
                    RoundRect(
                        rect = Rect(0f, 0f, size.width, size.height - size.height * 0.1f),
                        cornerRadius = CornerRadius(size.height / 2f)
                    )
                )

                moveTo(size.width / 2f - size.width * 0.05f, size.height - size.height * 0.1f)
                lineTo(size.width / 2f, size.height)
                lineTo(size.width / 2f + size.width * 0.05f, size.height - size.height * 0.1f)

                close()
            }
        )
    }
}
