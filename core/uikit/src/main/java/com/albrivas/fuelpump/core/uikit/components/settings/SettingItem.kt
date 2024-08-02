package com.albrivas.fuelpump.core.uikit.components.settings

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.albrivas.fuelpump.core.uikit.R
import com.albrivas.fuelpump.core.uikit.theme.GrayLight
import com.albrivas.fuelpump.core.uikit.theme.MyApplicationTheme
import com.albrivas.fuelpump.core.uikit.theme.primaryContainerLight

@Composable
fun SettingItem(model: SettingItemModel, modifier: Modifier = Modifier) = with(model) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = 16.dp, end = 16.dp)
            .clickable { onClick() },
        verticalAlignment = Alignment.CenterVertically
    ) {
        Card(
            shape = RoundedCornerShape(8.dp),
            colors = CardDefaults.cardColors(containerColor = primaryContainerLight)
        ) {
            Image(
                modifier = Modifier.padding(8.dp),
                painter = painterResource(id = icon),
                contentDescription = "fuel setting"
            )
        }

        Column(
            modifier = modifier
                .weight(80f)
                .padding(start = 16.dp, end = 16.dp),
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
        Icon(
            painter = painterResource(id = R.drawable.ic_chevron_right),
            contentDescription = "chevron right"
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun SettingItemPreview() {
    MyApplicationTheme {
        SettingItem(
            model = SettingItemModel(
                title = "Combustible seleccionado",
                selection = "Gasolina 95",
                icon = R.drawable.ic_map
            )
        )
    }
}
