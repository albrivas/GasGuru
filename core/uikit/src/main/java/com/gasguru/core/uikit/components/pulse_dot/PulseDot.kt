package com.gasguru.core.uikit.components.pulse_dot

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

@Composable
fun PulseDot(color: Color, modifier: Modifier = Modifier) {
    val infiniteTransition = rememberInfiniteTransition(label = "pulseDot")
    val rippleScale by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = 2.4f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 1400, easing = LinearEasing),
            repeatMode = RepeatMode.Restart,
        ),
        label = "rippleScale",
    )
    val rippleAlpha by infiniteTransition.animateFloat(
        initialValue = 0.5f,
        targetValue = 0f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 1400, easing = LinearEasing),
            repeatMode = RepeatMode.Restart,
        ),
        label = "rippleAlpha",
    )
    Box(
        contentAlignment = Alignment.Center,
        modifier = modifier,
    ) {
        Box(
            modifier = Modifier
                .size(8.dp)
                .scale(rippleScale)
                .alpha(rippleAlpha)
                .clip(CircleShape)
                .background(color),
        )
        Box(
            modifier = Modifier
                .size(5.dp)
                .clip(CircleShape)
                .background(color),
        )
    }
}
