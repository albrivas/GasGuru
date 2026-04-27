package com.gasguru.core.uikit.components.swipe

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.Icon
import androidx.compose.material3.SwipeToDismissBox
import androidx.compose.material3.SwipeToDismissBoxValue
import androidx.compose.material3.rememberSwipeToDismissBoxState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.gasguru.core.uikit.components.icon.FuelStationIcons
import com.gasguru.core.uikit.components.fuelItem.FuelStationItem
import com.gasguru.core.uikit.components.fuelItem.FuelStationItemModel
import com.gasguru.core.uikit.theme.GasGuruTheme
import com.gasguru.core.uikit.theme.MyApplicationTheme
import com.gasguru.core.uikit.theme.ThemePreviews

@Composable
fun SwipeItem(
    modifier: Modifier = Modifier,
    model: SwipeItemModel,
    content: @Composable () -> Unit,
) = with(model) {
    val swipeState = rememberSwipeToDismissBoxState(
        confirmValueChange = { value ->
            when (value) {
                SwipeToDismissBoxValue.StartToEnd,
                SwipeToDismissBoxValue.EndToStart,
                -> onClick()
                SwipeToDismissBoxValue.Settled -> return@rememberSwipeToDismissBoxState false
            }
            return@rememberSwipeToDismissBoxState true
        },
        positionalThreshold = { it * .50f },
    )

    val backgroundColorAlpha = when {
        swipeState.progress > 0 -> backgroundColor.copy(alpha = swipeState.progress * 6f)
        swipeState.progress < 0 -> backgroundColor.copy(alpha = -swipeState.progress * 6f)
        else -> backgroundColor.copy(alpha = 0f)
    }

    SwipeToDismissBox(
        modifier = modifier,
        enableDismissFromEndToStart = enableDismissFromEndToStart,
        enableDismissFromStartToEnd = enableDismissFromStartToEnd,
        state = swipeState,
        backgroundContent = {
            Row(
                modifier = Modifier
                    .fillMaxSize()
                    .background(backgroundColorAlpha),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    modifier = Modifier
                        .padding(24.dp)
                        .alpha(backgroundColorAlpha.alpha),
                    tint = Color.White,
                )
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    modifier = Modifier
                        .padding(24.dp)
                        .alpha(backgroundColorAlpha.alpha),
                    tint = Color.White,
                )
            }
        },
    ) {
        content()
    }
}

@Composable
@ThemePreviews
private fun SwipeItemPreview() {
    MyApplicationTheme {
        SwipeItem(
            model = SwipeItemModel(
                icon = Icons.Default.Delete,
                backgroundColor = Color.Red,
                onClick = {},
            ),
        ) {
            FuelStationItem(
                model = FuelStationItemModel(
                    idServiceStation = 1,
                    icon = FuelStationIcons.Repsol,
                    name = "EDAN REPSOL",
                    distance = "567 m",
                    price = "1.75 €/l",
                    index = 3686,
                    categoryColor = GasGuruTheme.colors.red500,
                    onItemClick = {},
                ),
                isLastItem = false,
            )
        }
    }
}
