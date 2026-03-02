package com.gasguru.core.uikit.components.tank_cost_card

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
import androidx.compose.material.icons.outlined.DirectionsCar
import androidx.compose.material.icons.outlined.Tune
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.gasguru.core.uikit.R
import com.gasguru.core.uikit.components.divider.DividerLength
import com.gasguru.core.uikit.components.divider.DividerThickness
import com.gasguru.core.uikit.components.divider.GasGuruDivider
import com.gasguru.core.uikit.components.divider.GasGuruDividerModel
import com.gasguru.core.uikit.components.fuel_type_chip.FuelTypeChip
import com.gasguru.core.uikit.components.fuel_type_chip.FuelTypeChipModel
import com.gasguru.core.uikit.theme.GasGuruTheme
import com.gasguru.core.uikit.theme.MyApplicationTheme
import com.gasguru.core.uikit.theme.ThemePreviews

@Composable
fun TankCostCard(model: TankCostCardModel, modifier: Modifier = Modifier) = with(model) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .border(
                width = 1.dp,
                color = GasGuruTheme.colors.neutral300,
                shape = RoundedCornerShape(16.dp),
            )
            .clip(RoundedCornerShape(16.dp))
            .background(GasGuruTheme.colors.neutralWhite)
            .padding(all = 20.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                text = stringResource(R.string.full_tank_cost),
                style = GasGuruTheme.typography.baseRegular,
                color = GasGuruTheme.colors.textMain,
            )
            FuelTypeChip(model = fuelTypeChip)
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.Bottom,
        ) {
            Text(
                modifier = Modifier.testTag("tank_cost_price"),
                text = totalCost,
                style = GasGuruTheme.typography.h1.copy(fontSize = 40.sp),
                color = GasGuruTheme.colors.primary800,
            )
            Column(horizontalAlignment = Alignment.End) {
                Text(
                    modifier = Modifier.testTag("tank_cost_litres"),
                    text = litres,
                    style = GasGuruTheme.typography.smallRegular,
                    color = GasGuruTheme.colors.textSubtle,
                )
                Text(
                    modifier = Modifier.testTag("tank_cost_price_per_litre"),
                    text = pricePerLitre,
                    style = GasGuruTheme.typography.smallBold,
                    color = GasGuruTheme.colors.textMain,
                )
            }
        }

        GasGuruDivider(
            model = GasGuruDividerModel(
                color = GasGuruTheme.colors.neutral300,
                thickness = DividerThickness.THICK,
                length = DividerLength.INSET
            )
        )

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Row(
                modifier = Modifier.weight(1f),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(6.dp),
            ) {
                Icon(
                    modifier = Modifier.size(16.dp),
                    imageVector = Icons.Outlined.DirectionsCar,
                    contentDescription = null,
                    tint = GasGuruTheme.colors.textSubtle,
                )
                Text(
                    modifier = Modifier.testTag("tank_cost_vehicle_name"),
                    text = vehicleName,
                    style = GasGuruTheme.typography.captionRegular,
                    color = GasGuruTheme.colors.textSubtle,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
            }
            Row(
                modifier = Modifier
                    .clip(RoundedCornerShape(8.dp))
                    .background(GasGuruTheme.colors.neutral200)
                    .clickable { onEditClick() }
                    .testTag("tank_cost_edit_button")
                    .padding(horizontal = 12.dp, vertical = 6.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(4.dp),
            ) {
                Icon(
                    modifier = Modifier.size(16.dp),
                    imageVector = Icons.Outlined.Tune,
                    contentDescription = null,
                    tint = GasGuruTheme.colors.textSubtle,
                )
                Text(
                    text = stringResource(R.string.change_litres),
                    style = GasGuruTheme.typography.captionRegular,
                    color = GasGuruTheme.colors.textSubtle,
                )
            }
        }
    }
}

@Composable
@ThemePreviews
private fun TankCostCardPreview() {
    MyApplicationTheme {
        TankCostCard(
            model = TankCostCardModel(
                fuelTypeChip = FuelTypeChipModel(
                    iconRes = R.drawable.ic_gasoline_95,
                    nameRes = R.string.preview_fuel_type,
                ),
                totalCost = "86.72 €",
                litres = "55.6 L",
                pricePerLitre = "1.559 €/l",
                vehicleName = "Golf VIII",
            ),
        )
    }
}
