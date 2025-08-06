package com.gasguru.core.uikit.components.divider

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.HorizontalDivider
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.gasguru.core.uikit.theme.MyApplicationTheme
import com.gasguru.core.uikit.theme.ThemePreviews

@Composable
fun GasGuruDivider(model: GasGuruDividerModel, modifier: Modifier = Modifier) = with(model) {
    val internalModifier = when (length) {
        DividerLength.FULL -> Modifier
        DividerLength.INSET -> Modifier.padding(16.dp)
    }
    HorizontalDivider(
        thickness = thickness.value,
        color = color,
        modifier = internalModifier.then(modifier)
    )
}

@Composable
@ThemePreviews
private fun GasGuruDividerPreview() {
    MyApplicationTheme {
        GasGuruDivider(
            model = GasGuruDividerModel(
                color = Color.Gray,
                length = DividerLength.INSET,
                thickness = DividerThickness.MEDIUM
            )
        )
    }
}
