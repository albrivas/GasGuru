package com.albrivas.fuelpump.core.uikit.components.settings

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.albrivas.fuelpump.core.uikit.theme.GrayLight
import com.albrivas.fuelpump.core.uikit.theme.MyApplicationTheme

@Composable
fun SettingItem(model: SettingItemModel, modifier: Modifier = Modifier) = with(model) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(start = 16.dp, end = 16.dp)
            .clickable { onClick() },
        Arrangement.spacedBy(6.dp)
    ) {
        Text(
            style = MaterialTheme.typography.bodySmall,
            text = title,
            fontWeight = FontWeight.Bold
        )
        Text(
            text = selection,
            color = GrayLight,
            style = MaterialTheme.typography.displaySmall
        )
    }
}

@Preview
@Composable
private fun SettingItemPreview() {
    MyApplicationTheme {
        SettingItem(
            model = SettingItemModel(
                title = "Combustible seleccionado",
                selection = "Gasolina 95"
            )
        )
    }
}
