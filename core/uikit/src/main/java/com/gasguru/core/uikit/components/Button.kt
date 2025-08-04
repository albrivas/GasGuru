package com.gasguru.core.uikit.components

import android.content.res.Configuration
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.gasguru.core.uikit.theme.GasGuruTheme

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

@Preview(showBackground = true)
@Composable
private fun GasGuruButtonPreview() {
    GasGuruButton(onClick = {}, text = "Siguiente", enabled = false)
}

@Preview(showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
private fun GasGuruButtonPreviewDarkMode() {
    GasGuruButton(onClick = {}, text = "Siguiente", enabled = false)
}
