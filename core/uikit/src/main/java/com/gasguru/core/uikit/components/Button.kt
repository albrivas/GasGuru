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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.gasguru.core.uikit.theme.GasGuruTheme
import com.gasguru.core.uikit.theme.ThemePreviews

@Composable
fun GasGuruButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    text: String,
    containerColor: Color = GasGuruTheme.colors.primary500,
    contentColor: Color = GasGuruTheme.colors.textContrast,
    shape: Shape = RoundedCornerShape(size = 8.dp),
    height: Dp = 50.dp,
    textStyle: TextStyle = GasGuruTheme.typography.baseBold,
    contentPadding: PaddingValues = ButtonDefaults.ContentPadding,
) {
    Button(
        onClick = onClick,
        shape = shape,
        modifier = modifier
            .fillMaxWidth()
            .height(height = height),
        enabled = enabled,
        contentPadding = contentPadding,
        colors = ButtonDefaults.buttonColors(
            containerColor = containerColor,
            contentColor = contentColor,
            disabledContentColor = GasGuruTheme.colors.neutral700,
            disabledContainerColor = GasGuruTheme.colors.neutral400,
        ),
    ) {
        Text(
            text = text,
            style = textStyle,
            color = contentColor,
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
