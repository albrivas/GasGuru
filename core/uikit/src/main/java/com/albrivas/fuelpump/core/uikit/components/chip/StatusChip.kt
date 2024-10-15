package com.albrivas.fuelpump.core.uikit.components.chip

import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.albrivas.fuelpump.core.uikit.theme.FuelPumpTheme
import com.albrivas.fuelpump.core.uikit.theme.MyApplicationTheme

@Composable
fun StatusChip(model: StatusChipModel, modifier: Modifier = Modifier) = with(model) {
    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(16.dp),
        color = color.copy(alpha = 0.16f),
    ) {
        Text(
            modifier = Modifier.padding(start = 12.dp, top = 4.dp, end = 12.dp, bottom = 4.dp),
            text = text,
            color = color,
            style = FuelPumpTheme.typography.baseRegular
        )
    }
}

@Preview
@Composable
private fun StatusChipPreview() {
    MyApplicationTheme {
        StatusChip(
            model = StatusChipModel(
                text = "Open",
                color = Color.Green,
            )
        )
    }
}
