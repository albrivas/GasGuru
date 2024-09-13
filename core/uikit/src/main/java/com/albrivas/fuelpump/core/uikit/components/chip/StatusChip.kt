package com.albrivas.fuelpump.core.uikit.components.chip

import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.albrivas.fuelpump.core.uikit.theme.MyApplicationTheme

@Composable
fun StatusChip(model: StatusChipModel, modifier: Modifier = Modifier) = with(model) {
    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(16.dp),
        color = color,
    ) {
        Text(
            text = text,
            color = Color.White,
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 0.dp),
            style = MaterialTheme.typography.labelMedium
        )
    }
}

@Preview
@Composable
private fun StatusChipPreview() {
    MyApplicationTheme {
        StatusChip(model = StatusChipModel(text = "Open", color = Color.Green))
    }
}
