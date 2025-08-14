package com.gasguru.core.uikit.utils

import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.testTagsAsResourceId
import androidx.compose.ui.unit.dp
import androidx.compose.ui.semantics.testTag as semanticsTestTag

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

/**
 * Custom modifier to add testTag and semantics for Maestro Mobile testing.
 * 
 * This modifier adds both testTag (for Compose UI testing) and semantics with testTagsAsResourceId
 * to make the element easily discoverable by Maestro Mobile and other Android testing tools.
 * The testTagsAsResourceId = true converts the tag to a native Android resource ID for better
 * performance and compatibility.
 * 
 * @param tag The unique identifier for the element in Maestro tests
 * @return Modifier with testTag and semantics configured for testing
 * 
 * Usage example:
 * ```kotlin
 * Button(
 *     modifier = Modifier.maestroTestTag("login_button"),
 *     onClick = { /* action */ }
 * ) {
 *     Text("Login")
 * }
 * ```
 * 
 * In Maestro tests:
 * ```yaml
 * - tapOn:
 *     id: "login_button"
 * ```
 */
fun Modifier.maestroTestTag(tag: String): Modifier = this
    .testTag(tag)
    .semantics {
        semanticsTestTag = tag
        testTagsAsResourceId = true
    }
