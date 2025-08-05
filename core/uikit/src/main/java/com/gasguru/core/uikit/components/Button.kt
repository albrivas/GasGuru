package com.gasguru.core.uikit.components

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.gasguru.core.uikit.theme.GasGuruTheme
import com.gasguru.core.uikit.theme.ThemePreviews

@Composable
fun GasGuruButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    text: String,
    contentPadding: PaddingValues = ButtonDefaults.ContentPadding,
) {
    Button(
        onClick = onClick,
        shape = RoundedCornerShape(8.dp),
        modifier = modifier
            .fillMaxWidth()
            .height(50.dp),
        enabled = enabled,
        contentPadding = contentPadding,
        colors = ButtonDefaults.buttonColors(
            containerColor = GasGuruTheme.colors.primary500,
            disabledContentColor = GasGuruTheme.colors.neutral700,
            disabledContainerColor = GasGuruTheme.colors.neutral400
        )
    ) {
        Text(
            text = text,
            style = GasGuruTheme.typography.baseBold
        )
    }
}

@Composable
@ThemePreviews
private fun GasGuruButtonPreview() {
    GasGuruButton(onClick = {}, text = "Siguiente", enabled = false)
}

@Composable
@ThemePreviews
private fun GasGuruButtonEnabledPreview() {
    GasGuruButton(onClick = {}, text = "Siguiente", enabled = true)
}

