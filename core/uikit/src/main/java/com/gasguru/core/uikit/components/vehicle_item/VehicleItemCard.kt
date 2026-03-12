package com.gasguru.core.uikit.components.vehicle_item

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
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
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.gasguru.core.uikit.R
import com.gasguru.core.uikit.theme.GasGuruTheme
import com.gasguru.core.uikit.theme.MyApplicationTheme
import com.gasguru.core.uikit.theme.ThemePreviews

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
            .clip(RoundedCornerShape(0.dp))
            .padding(all = 12.dp),
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
                    painter = painterResource(id = model.vehicleTypeIconRes),
                    contentDescription = null,
                    modifier = Modifier.size(18.dp),
                    colorFilter = ColorFilter.tint(GasGuruTheme.colors.primary700),
                )
            }
            Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(5.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Text(
                        text = model.name ?: stringResource(id = R.string.vehicle_default_name),
                        style = GasGuruTheme.typography.baseBold,
                        color = GasGuruTheme.colors.textMain,
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    LiveDot()
                    Spacer(modifier = Modifier.width(2.dp))
                    Text(
                        text = stringResource(id = R.string.vehicle_in_use),
                        style = GasGuruTheme.typography.captionRegular,
                        color = GasGuruTheme.colors.primary700,
                    )
                }
                Text(
                    text = stringResource(
                        id = R.string.vehicle_fuel_capacity,
                        stringResource(id = model.fuelTypeTranslationRes),
                        model.tankCapacityLitres,
                    ),
                    style = GasGuruTheme.typography.smallRegular,
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
            .clip(RoundedCornerShape(0.dp))
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
                    painter = painterResource(id = model.vehicleTypeIconRes),
                    contentDescription = null,
                    modifier = Modifier.size(18.dp),
                    colorFilter = ColorFilter.tint(GasGuruTheme.colors.neutral800),
                )
            }
            Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                Text(
                    text = model.name ?: stringResource(id = R.string.vehicle_default_name),
                    style = GasGuruTheme.typography.baseBold,
                    color = GasGuruTheme.colors.textMain,
                )
                Text(
                    text = stringResource(
                        id = R.string.vehicle_fuel_capacity,
                        stringResource(id = model.fuelTypeTranslationRes),
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
private fun LiveDot() {
    val infiniteTransition = rememberInfiniteTransition(label = "liveDot")
    val rippleScale by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = 2.4f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 1400, easing = LinearEasing),
            repeatMode = RepeatMode.Restart,
        ),
        label = "rippleScale",
    )
    val rippleAlpha by infiniteTransition.animateFloat(
        initialValue = 0.5f,
        targetValue = 0f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 1400, easing = LinearEasing),
            repeatMode = RepeatMode.Restart,
        ),
        label = "rippleAlpha",
    )
    Box(contentAlignment = Alignment.Center) {
        Box(
            modifier = Modifier
                .size(8.dp)
                .scale(rippleScale)
                .alpha(rippleAlpha)
                .clip(CircleShape)
                .background(GasGuruTheme.colors.primary500),
        )
        Box(
            modifier = Modifier
                .size(5.dp)
                .clip(CircleShape)
                .background(GasGuruTheme.colors.primary500),
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
                    vehicleTypeIconRes = R.drawable.ic_vehicle_car,
                    fuelTypeTranslationRes = R.string.preview_fuel_type,
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
                    vehicleTypeIconRes = R.drawable.ic_vehicle_motorcycle,
                    fuelTypeTranslationRes = R.string.preview_fuel_type,
                    tankCapacityLitres = 18,
                    isSelected = false,
                ),
            )
        }
    }
}
