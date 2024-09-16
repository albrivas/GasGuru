package com.albrivas.fuelpump.core.uikit.components.fuelItem

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CornerSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.MaterialTheme.typography
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.albrivas.fuelpump.core.uikit.R
import com.albrivas.fuelpump.core.uikit.theme.GrayExtraLight
import com.albrivas.fuelpump.core.uikit.theme.MyApplicationTheme
import com.albrivas.fuelpump.core.uikit.theme.PriceExpensive
import com.albrivas.fuelpump.core.uikit.utils.backgroundColor
import java.util.Locale

@Composable
fun FuelStationItem(
    modifier: Modifier = Modifier,
    model: FuelStationItemModel
) = with(model) {
    val contentDescription = stringResource(id = R.string.content_description_fuel_item, index)
    ElevatedCard(
        modifier = modifier
            .fillMaxWidth()
            .padding(6.dp)
            .clip(
                shape = RoundedCornerShape(
                    topStart = CornerSize(8.dp),
                    topEnd = CornerSize(0.dp),
                    bottomStart = CornerSize(8.dp),
                    bottomEnd = CornerSize(0.dp)
                )
            )
            .clickable { onItemClick(idServiceStation) }
            .semantics { this.contentDescription = contentDescription },
        elevation = CardDefaults.cardElevation(defaultElevation = 16.dp),
        shape = RoundedCornerShape(
            topStart = CornerSize(8.dp),
            topEnd = CornerSize(0.dp),
            bottomStart = CornerSize(8.dp),
            bottomEnd = CornerSize(0.dp)
        )

    ) {
        Row(
            modifier = Modifier
                .background(Color.White)
                .fillMaxWidth()
                .height(IntrinsicSize.Max)
        ) {
            Box(
                modifier = Modifier
                    .background(categoryColor)
                    .width(8.dp)
                    .fillMaxHeight()
            )
            Column(
                modifier = Modifier
                    .padding(16.dp)
                    .align(Alignment.CenterVertically)
            ) {
                Image(
                    modifier = Modifier.size(40.dp),
                    painter = painterResource(id = icon),
                    contentDescription = "Fuel station brand"
                )
            }
            Column(
                modifier = Modifier
                    .padding(end = 16.dp, top = 16.dp, bottom = 16.dp)
                    .align(Alignment.CenterVertically)
                    .weight(1f)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        modifier = Modifier.weight(0.6f),
                        text = name,
                        style = typography.labelMedium,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    Text(
                        modifier = Modifier.weight(0.4f),
                        text = distance,
                        style = typography.labelSmall,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
                Text(
                    text = direction.lowercase(Locale.getDefault()).replaceFirstChar {
                        if (it.isLowerCase()) {
                            it.titlecase(
                                Locale.getDefault()
                            )
                        } else {
                            it.toString()
                        }
                    },
                    maxLines = 2,
                    style = typography.labelMedium,
                    color = GrayExtraLight,
                    overflow = TextOverflow.Ellipsis
                )
            }
            val boxContentDesc =
                stringResource(id = R.string.content_description_fuel_item_price_box)
            Box(
                modifier = Modifier
                    .background(
                        color = categoryColor,
                        shape = RoundedCornerShape(
                            topStart = CornerSize(4.dp),
                            topEnd = CornerSize(0.dp),
                            bottomStart = CornerSize(0.dp),
                            bottomEnd = CornerSize(0.dp)
                        )
                    )
                    .padding(4.dp)
                    .width(74.dp)
                    .fillMaxHeight()
                    .align(Alignment.CenterVertically)
                    .semantics {
                        this.contentDescription = boxContentDesc
                        backgroundColor = categoryColor
                    }
            ) {
                Text(
                    text = "$price €/L",
                    color = Color.White,
                    style = typography.labelLarge,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier
                        .padding(4.dp)
                        .align(Alignment.Center)

                )
            }
        }
    }
}

@Composable
@Preview(showBackground = true, backgroundColor = 0xFFFFFF)
fun PreviewFuelItem() {
    MyApplicationTheme {
        FuelStationItem(
            model = FuelStationItemModel(
                idServiceStation = 1937,
                icon = R.drawable.ic_logo_q8,
                name = "Q8",
                direction = "Calle de santiago ",
                distance = "567 m",
                price = "1.67",
                index = 3686,
                categoryColor = PriceExpensive,
                onItemClick = {}
            )
        )
    }
}
