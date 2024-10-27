package com.albrivas.fuelpump.core.uikit.components.settings

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.albrivas.fuelpump.core.uikit.R
import com.albrivas.fuelpump.core.uikit.theme.FuelPumpTheme
import com.albrivas.fuelpump.core.uikit.theme.MyApplicationTheme
import com.albrivas.fuelpump.core.uikit.theme.Neutral300
import com.albrivas.fuelpump.core.uikit.theme.Neutral500
import com.albrivas.fuelpump.core.uikit.theme.TextSubtle

@Composable
fun SettingItem(model: SettingItemModel, modifier: Modifier = Modifier) = with(model) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(8.dp))
            .background(color = Color.White)
            .clickable { onClick() }
            .border(1.dp, Neutral300, RoundedCornerShape(8.dp))
            .padding(12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Image(
            modifier = Modifier.size(32.dp),
            painter = painterResource(id = icon),
            contentDescription = "fuel setting"
        )

        Column(
            modifier = Modifier
                .weight(80f)
                .padding(start = 16.dp, end = 16.dp),
            Arrangement.spacedBy(6.dp)
        ) {
            Text(
                style = FuelPumpTheme.typography.baseRegular,
                text = title,
            )
            Text(
                text = selection,
                color = TextSubtle,
                style = FuelPumpTheme.typography.smallRegular
            )
        }
        Icon(
            imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
            contentDescription = "chevron right",
            tint = Neutral500
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
