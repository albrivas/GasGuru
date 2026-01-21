package com.gasguru.core.uikit.components.settings

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
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.gasguru.core.uikit.R
import com.gasguru.core.uikit.theme.GasGuruTheme
import com.gasguru.core.uikit.theme.MyApplicationTheme
import com.gasguru.core.uikit.theme.ThemePreviews

@Composable
fun SettingItem(
    model: SettingItemModel,
    modifier: Modifier = Modifier,
) = with(model) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(8.dp))
            .background(color = GasGuruTheme.colors.neutralWhite)
            .clickable { onClick() }
            .border(1.dp, GasGuruTheme.colors.neutral300, RoundedCornerShape(8.dp))
            .padding(12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Image(
            modifier = Modifier.size(32.dp),
            painter = painterResource(id = icon),
            contentDescription = "fuel setting",
            colorFilter = ColorFilter.tint(
                color = GasGuruTheme.colors.neutralBlack
            )
        )

        Column(
            modifier = Modifier
                .weight(80f)
                .padding(start = 16.dp, end = 16.dp),
            Arrangement.spacedBy(6.dp)
        ) {
            Text(
                style = GasGuruTheme.typography.baseRegular,
                text = title,
                color = GasGuruTheme.colors.textMain
            )
            Text(
                text = selection,
                color = GasGuruTheme.colors.textSubtle,
                style = GasGuruTheme.typography.smallRegular
            )
        }
        Icon(
            imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
            contentDescription = "chevron right",
            tint = GasGuruTheme.colors.neutral500
        )
    }
}

@Composable
@ThemePreviews
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
