package com.albrivas.fuelpump.feature.fuel_list_station.ui

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
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.albrivas.fuelpump.core.model.data.FuelStation
import com.albrivas.fuelpump.core.model.data.FuelType
import com.albrivas.fuelpump.core.model.data.previewFuelStationDomain
import com.albrivas.fuelpump.core.ui.getPrice
import com.albrivas.fuelpump.core.ui.toBrandStationIcon
import com.albrivas.fuelpump.core.ui.toColor
import com.albrivas.fuelpump.core.uikit.theme.GrayExtraLight
import com.albrivas.fuelpump.core.uikit.theme.MyApplicationTheme
import java.util.Locale

@Composable
fun FuelStationItem(
    item: FuelStation,
    userSelectedFuelType: FuelType,
    modifier: Modifier = Modifier,
    onItemClick: (FuelStation) -> Unit
) {
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
            .clickable { onItemClick(item) },
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
                    .background(item.priceCategory.toColor())
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
                    painter = painterResource(id = item.brandStationBrandsType.toBrandStationIcon()),
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
                        text = item.brandStationName,
                        style = typography.labelMedium,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    Text(
                        modifier = Modifier.weight(0.4f),
                        text = item.formatDistance(),
                        style = typography.labelSmall,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
                Text(
                    text = item.direction.lowercase(Locale.getDefault()).replaceFirstChar {
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

            Box(
                modifier = Modifier
                    .background(
                        color = item.priceCategory.toColor(),
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
            ) {
                Text(
                    text = "${userSelectedFuelType.getPrice(item)} €/L",
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
            item = previewFuelStationDomain(),
            userSelectedFuelType = FuelType.GASOLINE_95,
            onItemClick = {}
        )
    }
}
