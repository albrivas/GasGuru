package com.gasguru.core.uikit.components.fuel_type_chip

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.gasguru.core.uikit.R
import com.gasguru.core.uikit.theme.GasGuruTheme
import com.gasguru.core.uikit.theme.MyApplicationTheme
import com.gasguru.core.uikit.theme.ThemePreviews

@Composable
fun FuelTypeChip(model: FuelTypeChipModel, modifier: Modifier = Modifier) = with(model) {
    Row(
        modifier = modifier
            .clip(RoundedCornerShape(20.dp))
            .background(GasGuruTheme.colors.accentGreen.copy(alpha = 0.12f))
            .border(1.dp, GasGuruTheme.colors.accentGreen.copy(alpha = 0.30f), RoundedCornerShape(20.dp))
            .padding(horizontal = 10.dp, vertical = 5.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(6.dp),
    ) {
        Text(
            modifier = Modifier.testTag("fuel_type_chip_name"),
            text = stringResource(id = nameRes),
            color = GasGuruTheme.colors.accentGreen,
            style = GasGuruTheme.typography.captionBold,
        )
    }
}

@Composable
@ThemePreviews
private fun FuelTypeChipPreview() {
    MyApplicationTheme {
        FuelTypeChip(
            model = FuelTypeChipModel(
                nameRes = R.string.preview_fuel_type,
            ),
        )
    }
}
