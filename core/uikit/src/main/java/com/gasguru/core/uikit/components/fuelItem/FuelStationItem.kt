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
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.intl.Locale
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.text.toLowerCase
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.gasguru.core.uikit.R
import com.gasguru.core.uikit.components.chip.StatusChip
import com.gasguru.core.uikit.components.chip.StatusChipModel
import com.gasguru.core.uikit.theme.AccentRed
import com.gasguru.core.uikit.theme.GasGuruTheme
import com.gasguru.core.uikit.theme.MyApplicationTheme
import com.gasguru.core.uikit.theme.Neutral300
import com.gasguru.core.uikit.theme.Neutral500
import com.gasguru.core.uikit.theme.TextMain
import com.gasguru.core.uikit.theme.TextSubtle

@Composable
fun FuelStationItem(
    modifier: Modifier = Modifier,
    model: FuelStationItemModel,
) = with(model) {
    val contentDescription = stringResource(id = R.string.content_description_fuel_item, index)

    Row(
        modifier = modifier
            .fillMaxWidth()
            .background(color = Color.White)
            .clickable { onItemClick(idServiceStation) }
            .semantics { this.contentDescription = contentDescription }
            .padding(horizontal = 12.dp)
            .drawBehind {
                val lineY = size.height - 1.dp.toPx()
                drawLine(
                    color = Neutral300,
                    start = Offset(0f, lineY),
                    end = Offset(size.width, lineY),
                    strokeWidth = 1.dp.toPx()
                )
            },
    ) {
        Column(
            modifier = Modifier
                .align(Alignment.CenterVertically)
        ) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .border(width = 1.dp, color = Neutral300, shape = CircleShape)

            ) {
                Image(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(4.dp)
                        .clip(CircleShape),
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
                text = name.toLowerCase(Locale.current)
                    .replaceFirstChar {
                        if (it.isLowerCase()) {
                            it.titlecase(
                                java.util.Locale.getDefault()
                            )
                        } else {
                            it.toString()
                        }
                    },
                style = GasGuruTheme.typography.baseRegular,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                color = TextMain
            )
            Text(
                modifier = Modifier.testTag("station-distance"),
                text = distance,
                maxLines = 1,
                style = GasGuruTheme.typography.smallRegular,
                color = TextSubtle,
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
                tint = Neutral500,
                contentDescription = ""
            )
        }
    }
}

@Composable
@Preview(showBackground = true)
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
                categoryColor = AccentRed,
                onItemClick = {}
            )
        )
    }
}
