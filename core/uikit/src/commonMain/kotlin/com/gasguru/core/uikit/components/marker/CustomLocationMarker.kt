package com.gasguru.core.uikit.components.marker

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.gasguru.core.uikit.generated.resources.Res
import com.gasguru.core.uikit.generated.resources.ic_location_on
import com.gasguru.core.uikit.theme.GasGuruTheme
import com.gasguru.core.uikit.theme.MyApplicationTheme
import com.gasguru.core.uikit.theme.ThemePreviews
import org.jetbrains.compose.resources.painterResource

private val PIN_SIZE = 44.dp
private val PIN_DOT_SIZE = 12.dp

// The pin tip sits at the bottom-center of the composable: anchor it at (0.5, 1) on the map.
@Composable
fun CustomLocationMarker(model: CustomLocationMarkerModel, modifier: Modifier = Modifier) {
    Box(modifier = modifier.size(PIN_SIZE)) {
        // White fill behind the transparent hole of the pin glyph (centered at ~41.7% of its height).
        Box(
            modifier = Modifier
                .align(Alignment.TopCenter)
                .offset(y = PIN_SIZE * 0.417f - PIN_DOT_SIZE / 2)
                .size(PIN_DOT_SIZE)
                .background(color = GasGuruTheme.colors.neutralWhite, shape = CircleShape),
        )
        Icon(
            painter = painterResource(Res.drawable.ic_location_on),
            contentDescription = model.contentDescription,
            tint = GasGuruTheme.colors.primary800,
            modifier = Modifier.size(PIN_SIZE),
        )
    }
}

@Composable
@ThemePreviews
private fun CustomLocationMarkerPreview() {
    MyApplicationTheme {
        CustomLocationMarker(model = CustomLocationMarkerModel(contentDescription = null))
    }
}
