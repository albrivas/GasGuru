package com.gasguru.core.uikit.components.icon

import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.gasguru.core.uikit.theme.GasGuruTheme
import com.gasguru.core.uikit.theme.ThemePreviews

@Composable
fun CircleIcon(model: CircleIconModel) = with(model) {
    Box(
        modifier = Modifier
            .size(32.dp)
            .clip(CircleShape)
            .border(width = 1.dp, color = GasGuruTheme.colors.neutral300, shape = CircleShape)
    ) {
        Image(
            modifier = Modifier
                .fillMaxWidth()
                .padding(4.dp)
                .clip(CircleShape)
                .align(Alignment.Center),
            painter = painterResource(id = icon),
            contentDescription = "Fuel station brand"
        )
    }
}

@Composable
@ThemePreviews
private fun CircleIconPreview() {
    CircleIcon(model = CircleIconModel(icon = FuelStationIcons.Repsol, size = 32.dp))
}
