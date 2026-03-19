package com.gasguru.core.uikit.components.vehicle_type

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.gasguru.core.uikit.R
import com.gasguru.core.uikit.theme.GasGuruTheme
import com.gasguru.core.uikit.theme.MyApplicationTheme
import com.gasguru.core.uikit.theme.ThemePreviews

@Composable
fun VehicleTypeCard(
    model: VehicleTypeCardModel,
    modifier: Modifier = Modifier,
) {
    val backgroundColor = if (model.isSelected) {
        GasGuruTheme.colors.primary100.copy(alpha = 0.4f)
    } else {
        GasGuruTheme.colors.neutral200
    }
    val contentColor = if (model.isSelected) {
        GasGuruTheme.colors.primary500
    } else {
        GasGuruTheme.colors.textSubtle
    }
    val shape = RoundedCornerShape(size = 12.dp)

    Column(
        modifier = modifier
            .clip(shape = shape)
            .background(color = backgroundColor)
            .then(
                if (model.isSelected) {
                    Modifier.border(
                        width = 2.dp,
                        color = GasGuruTheme.colors.primary500,
                        shape = shape,
                    )
                } else {
                    Modifier
                },
            )
            .clickable { model.onClick() }
            .padding(vertical = 12.dp, horizontal = 8.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(space = 6.dp),
    ) {
        Icon(
            painter = painterResource(id = model.iconRes),
            contentDescription = stringResource(id = model.nameRes),
            tint = contentColor,
            modifier = Modifier.size(size = 24.dp),
        )
        Text(
            text = stringResource(id = model.nameRes),
            style = GasGuruTheme.typography.captionBold,
            color = contentColor,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            textAlign = TextAlign.Center,
        )
    }
}

@Composable
@ThemePreviews
private fun VehicleTypeCardPreview() {
    MyApplicationTheme {
        VehicleTypeCard(
            model = VehicleTypeCardModel(
                iconRes = R.drawable.ic_vehicle_car,
                nameRes = R.string.preview_fuel_type,
                isSelected = true,
                onClick = {},
            ),
        )
    }
}

@Composable
@ThemePreviews
private fun VehicleTypeCardUnselectedPreview() {
    MyApplicationTheme {
        VehicleTypeCard(
            model = VehicleTypeCardModel(
                iconRes = R.drawable.ic_vehicle_car,
                nameRes = R.string.preview_fuel_type,
                isSelected = false,
                onClick = {},
            ),
        )
    }
}
