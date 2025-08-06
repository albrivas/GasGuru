package com.gasguru.core.uikit.components.fuelItem

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.gasguru.core.uikit.R
import com.gasguru.core.uikit.components.chip.StatusChip
import com.gasguru.core.uikit.components.chip.StatusChipModel
import com.gasguru.core.uikit.theme.GasGuruTheme
import com.gasguru.core.uikit.theme.MyApplicationTheme
import com.gasguru.core.uikit.theme.ThemePreviews
import com.gasguru.core.uikit.utils.horizontalDivider

@Composable
fun FuelStationItem(
    modifier: Modifier = Modifier,
    model: FuelStationItemModel,
    isLastItem: Boolean,
) = with(model) {
    val contentDescription = stringResource(id = R.string.content_description_fuel_item, index)
    val neutral300 = GasGuruTheme.colors.neutral300
    Row(
        modifier = modifier
            .fillMaxWidth()
            .background(color = GasGuruTheme.colors.neutralWhite)
            .clickable { onItemClick(idServiceStation) }
            .semantics { this.contentDescription = contentDescription }
            .padding(horizontal = 12.dp)
            .horizontalDivider(color = neutral300, isLastItem = isLastItem),
    ) {
        Column(
            modifier = Modifier
                .align(Alignment.CenterVertically)
        ) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .border(width = 1.dp, color = neutral300, shape = CircleShape)
            ) {
                Image(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(4.dp)
                        .clip(CircleShape)
                        .align(Alignment.Center),
                    painter = painterResource(id = icon),
                    contentDescription = "Fuel station brand"
                )
            }
        }
        Column(
            modifier = Modifier
                .padding(end = 16.dp, top = 16.dp, bottom = 16.dp, start = 12.dp)
                .align(Alignment.CenterVertically)
                .weight(0.4f)
        ) {
            Text(
                modifier = Modifier.testTag("station-name"),
                text = name,
                style = GasGuruTheme.typography.baseRegular,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                color = GasGuruTheme.colors.textMain
            )
            Text(
                modifier = Modifier.testTag("station-distance"),
                text = distance,
                maxLines = 1,
                style = GasGuruTheme.typography.smallRegular,
                color = GasGuruTheme.colors.textSubtle,
                overflow = TextOverflow.Ellipsis
            )
        }
        Column(
            modifier = Modifier
                .padding(end = 16.dp, top = 16.dp, bottom = 16.dp)
                .align(Alignment.CenterVertically)
                .weight(0.6f),
            horizontalAlignment = Alignment.End
        ) {
            StatusChip(
                modifier = Modifier.testTag("station-price"),
                model = StatusChipModel(
                    text = price,
                    color = categoryColor,
                )
            )
        }
        Column(
            modifier = Modifier
                .align(Alignment.CenterVertically)
        ) {
            Icon(
                imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                tint = GasGuruTheme.colors.neutral500,
                contentDescription = ""
            )
        }
    }
}

@Composable
@ThemePreviews
fun PreviewFuelItem() {
    MyApplicationTheme {
        FuelStationItem(
            model = FuelStationItemModel(
                idServiceStation = 1,
                icon = R.drawable.ic_logo_repsol,
                name = "EDAN REPSOL",
                distance = "567 m",
                price = "1.75 €/l",
                index = 3686,
                categoryColor = GasGuruTheme.colors.secondaryLight,
                onItemClick = {}
            ),
            isLastItem = false
        )
    }
}
