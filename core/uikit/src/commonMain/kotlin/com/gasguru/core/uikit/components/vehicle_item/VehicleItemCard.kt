package com.gasguru.core.uikit.components.vehicle_item

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
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
import androidx.compose.ui.unit.dp
import com.gasguru.core.uikit.components.pulse_dot.PulseDot
import com.gasguru.core.uikit.generated.resources.Res
import com.gasguru.core.uikit.generated.resources.ic_vehicle_car
import com.gasguru.core.uikit.generated.resources.ic_vehicle_motorcycle
import com.gasguru.core.uikit.generated.resources.preview_fuel_type
import com.gasguru.core.uikit.generated.resources.vehicle_default_name
import com.gasguru.core.uikit.generated.resources.vehicle_fuel_capacity
import com.gasguru.core.uikit.generated.resources.vehicle_in_use
import com.gasguru.core.uikit.theme.GasGuruTheme
import com.gasguru.core.uikit.theme.MyApplicationTheme
import com.gasguru.core.uikit.theme.ThemePreviews
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource

@Composable
fun VehicleItemCard(
    model: VehicleItemCardModel,
    modifier: Modifier = Modifier,
    onClick: (() -> Unit)? = null,
) {
    val clickableModifier = modifier.then(if (onClick != null) Modifier.clickable { onClick() } else Modifier)
    if (model.isSelected) {
        ActiveVehicleRow(model = model, modifier = clickableModifier)
    } else {
        InactiveVehicleRow(model = model, modifier = clickableModifier)
    }
}

@Composable
private fun ActiveVehicleRow(
    model: VehicleItemCardModel,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .background(color = GasGuruTheme.colors.neutralWhite)
            .padding(horizontal = 16.dp, vertical = 14.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Row(
            modifier = Modifier.weight(1f),
            horizontalArrangement = Arrangement.spacedBy(10.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Box(
                modifier = Modifier
                    .size(38.dp)
                    .clip(CircleShape)
                    .background(color = GasGuruTheme.colors.primary500.copy(alpha = 0.2f)),
                contentAlignment = Alignment.Center,
            ) {
                Image(
                    painter = painterResource(model.vehicleTypeIconRes),
                    contentDescription = null,
                    modifier = Modifier.size(24.dp),
                    colorFilter = ColorFilter.tint(GasGuruTheme.colors.primary700),
                )
            }
            Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(5.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Text(
                        text = model.name ?: stringResource(Res.string.vehicle_default_name),
                        style = GasGuruTheme.typography.baseBold,
                        color = GasGuruTheme.colors.textMain,
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    PulseDot(color = GasGuruTheme.colors.primary500)
                    Spacer(modifier = Modifier.width(2.dp))
                    Text(
                        text = stringResource(Res.string.vehicle_in_use),
                        style = GasGuruTheme.typography.captionRegular,
                        color = GasGuruTheme.colors.primary700,
                    )
                }
                Text(
                    text = stringResource(
                        Res.string.vehicle_fuel_capacity,
                        stringResource(model.fuelTypeTranslationRes),
                        model.tankCapacityLitres,
                    ),
                    style = GasGuruTheme.typography.captionRegular,
                    color = GasGuruTheme.colors.textSubtle,
                )
            }
        }
        Icon(
            imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
            contentDescription = null,
            tint = GasGuruTheme.colors.neutral600,
        )
    }
}

@Composable
private fun InactiveVehicleRow(
    model: VehicleItemCardModel,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .background(color = GasGuruTheme.colors.neutralWhite)
            .padding(horizontal = 16.dp, vertical = 14.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Row(
            modifier = Modifier.weight(1f),
            horizontalArrangement = Arrangement.spacedBy(10.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Box(
                modifier = Modifier
                    .size(38.dp)
                    .clip(CircleShape)
                    .background(color = GasGuruTheme.colors.neutral200),
                contentAlignment = Alignment.Center,
            ) {
                Image(
                    painter = painterResource(model.vehicleTypeIconRes),
                    contentDescription = null,
                    modifier = Modifier.size(24.dp),
                    colorFilter = ColorFilter.tint(GasGuruTheme.colors.neutral800),
                )
            }
            Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                Text(
                    text = model.name ?: stringResource(Res.string.vehicle_default_name),
                    style = GasGuruTheme.typography.baseBold,
                    color = GasGuruTheme.colors.textMain,
                )
                Text(
                    text = stringResource(
                        Res.string.vehicle_fuel_capacity,
                        stringResource(model.fuelTypeTranslationRes),
                        model.tankCapacityLitres,
                    ),
                    style = GasGuruTheme.typography.captionRegular,
                    color = GasGuruTheme.colors.textSubtle,
                )
            }
        }
        Icon(
            imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
            contentDescription = null,
            tint = GasGuruTheme.colors.neutral600,
        )
    }
}

@Composable
@ThemePreviews
private fun VehicleItemCardSelectedPreview() {
    MyApplicationTheme {
        Column(
            modifier = Modifier
                .clip(RoundedCornerShape(14.dp))
                .background(GasGuruTheme.colors.neutralWhite)
                .border(1.dp, GasGuruTheme.colors.neutral300, RoundedCornerShape(14.dp)),
        ) {
            VehicleItemCard(
                model = VehicleItemCardModel(
                    id = 1L,
                    name = "Golf VIII",
                    vehicleTypeIconRes = Res.drawable.ic_vehicle_car,
                    fuelTypeTranslationRes = Res.string.preview_fuel_type,
                    tankCapacityLitres = 55,
                    isSelected = true,
                ),
            )
        }
    }
}

@Composable
@ThemePreviews
private fun VehicleItemCardUnselectedPreview() {
    MyApplicationTheme {
        Column(
            modifier = Modifier
                .clip(RoundedCornerShape(14.dp))
                .background(GasGuruTheme.colors.neutralWhite)
                .border(1.dp, GasGuruTheme.colors.neutral300, RoundedCornerShape(14.dp)),
        ) {
            VehicleItemCard(
                model = VehicleItemCardModel(
                    id = 2L,
                    name = "Honda CB500",
                    vehicleTypeIconRes = Res.drawable.ic_vehicle_motorcycle,
                    fuelTypeTranslationRes = Res.string.preview_fuel_type,
                    tankCapacityLitres = 18,
                    isSelected = false,
                ),
            )
        }
    }
}
