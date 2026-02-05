package com.gasguru.core.uikit.components.route_navigation_card

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.Dimension
import com.gasguru.core.uikit.theme.GasGuruTheme
import com.gasguru.core.uikit.theme.MyApplicationTheme
import com.gasguru.core.uikit.theme.ThemePreviews

@Composable
fun RouteNavigationCard(
    model: RouteNavigationCardModel,
    modifier: Modifier = Modifier,
) = with(model) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .border(
                width = 1.dp,
                color = GasGuruTheme.colors.neutral400,
                shape = RoundedCornerShape(8.dp)
            )
            .clip(RoundedCornerShape(8.dp))
            .background(color = GasGuruTheme.colors.neutral100)
            .padding(all = 12.dp),
    ) {
        ConstraintLayout(
            modifier = Modifier
                .fillMaxWidth()
                .wrapContentHeight(),
        ) {
            val (contentGroup, closeIcon) = createRefs()

            Column(
                modifier = Modifier.constrainAs(contentGroup) {
                    top.linkTo(parent.top)
                    start.linkTo(parent.start)
                    end.linkTo(closeIcon.start, margin = 8.dp)
                    width = Dimension.fillToConstraints
                },
            ) {
                // Destination address (main/large text)
                Text(
                    text = destination,
                    style = GasGuruTheme.typography.h5,
                    overflow = TextOverflow.Ellipsis,
                    maxLines = 2,
                    color = GasGuruTheme.colors.textMain,
                )

                // Station count, distance and duration info
                Row(
                    modifier = Modifier.padding(top = 16.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    if (distance != null && duration != null) {
                        Text(
                            text = "$stationCount gasolineras",
                            style = GasGuruTheme.typography.smallRegular,
                            color = GasGuruTheme.colors.textSubtle,
                        )

                        Spacer(modifier = Modifier.width(8.dp))

                        Text(
                            text = "•",
                            style = GasGuruTheme.typography.smallRegular,
                            color = GasGuruTheme.colors.textSubtle,
                        )

                        Spacer(modifier = Modifier.width(8.dp))

                        Text(
                            text = distance,
                            style = GasGuruTheme.typography.smallRegular,
                            color = GasGuruTheme.colors.textSubtle,
                        )

                        Spacer(modifier = Modifier.width(8.dp))

                        Text(
                            text = "•",
                            style = GasGuruTheme.typography.smallRegular,
                            color = GasGuruTheme.colors.textSubtle,
                        )

                        Spacer(modifier = Modifier.width(8.dp))

                        Text(
                            text = duration,
                            style = GasGuruTheme.typography.smallRegular,
                            color = GasGuruTheme.colors.textSubtle,
                        )
                    } else {
                        Text(
                            text = "Calculando ruta...",
                            style = GasGuruTheme.typography.smallRegular,
                            color = GasGuruTheme.colors.textSubtle,
                        )
                    }
                }
            }

            // Close icon
            Icon(
                modifier = Modifier
                    .size(24.dp)
                    .clip(CircleShape)
                    .clickable { onClose() }
                    .constrainAs(closeIcon) {
                        top.linkTo(parent.top)
                        end.linkTo(parent.end)
                    },
                imageVector = Icons.Filled.Close,
                contentDescription = "Close route",
                tint = GasGuruTheme.colors.neutralBlack,
            )
        }
    }
}

@Composable
@ThemePreviews
private fun RouteNavigationCardPreview() {
    MyApplicationTheme {
        RouteNavigationCard(
            model = RouteNavigationCardModel(
                destination = "Calle Gran Vía, 28, Madrid",
                stationCount = 5,
                distance = "12,5 km",
                duration = "25 min",
                onClose = {},
            ),
        )
    }
}

@Composable
@ThemePreviews
private fun RouteNavigationCardLongDestinationPreview() {
    MyApplicationTheme {
        RouteNavigationCard(
            model = RouteNavigationCardModel(
                destination = "Avenida de la Constitución con Plaza Mayor y Paseo de la Castellana, 123, 5º B",
                stationCount = 15,
                distance = "45,8 km",
                duration = "1 h 30 min",
                onClose = {},
            ),
        )
    }
}

@Composable
@ThemePreviews
private fun RouteNavigationCardLoadingPreview() {
    MyApplicationTheme {
        RouteNavigationCard(
            model = RouteNavigationCardModel(
                destination = "Calle Gran Vía, 28, Madrid",
                stationCount = 0,
                distance = null,
                duration = null,
                onClose = {},
            ),
        )
    }
}
