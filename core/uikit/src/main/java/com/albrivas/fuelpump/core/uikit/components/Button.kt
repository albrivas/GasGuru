package com.albrivas.fuelpump.core.uikit.components

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
import com.albrivas.fuelpump.core.uikit.theme.FuelPumpTheme
import com.albrivas.fuelpump.core.uikit.theme.Primary500

@Composable
fun FuelPumpButton(
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
            containerColor = Primary500
        )
    ) {
        Text(
            text = text,
            style = FuelPumpTheme.typography.baseBold
        )
    }
}

@Preview
@Composable
private fun FuelPumpButtonPreview() {
    FuelPumpButton(onClick = {}, text = "Siguiente")
}
