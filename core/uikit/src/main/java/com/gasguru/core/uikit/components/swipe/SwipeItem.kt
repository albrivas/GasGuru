package com.gasguru.core.uikit.components.swipe

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.SwipeToDismissBox
import androidx.compose.material3.SwipeToDismissBoxValue
import androidx.compose.material3.rememberSwipeToDismissBoxState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.rememberLottieComposition
import com.gasguru.core.uikit.R
import com.gasguru.core.uikit.components.fuelItem.FuelStationItem
import com.gasguru.core.uikit.components.fuelItem.FuelStationItemModel
import com.gasguru.core.uikit.theme.MyApplicationTheme
import com.gasguru.core.uikit.theme.Red500

@Composable
fun SwipeItem(modifier: Modifier = Modifier, model: SwipeItemModel) = with(model) {
    val swipeState = rememberSwipeToDismissBoxState()
    val composition by rememberLottieComposition(LottieCompositionSpec.RawRes(iconAnimated))

    when (swipeState.currentValue) {
        SwipeToDismissBoxValue.StartToEnd -> {}

        SwipeToDismissBoxValue.EndToStart -> {
            onClick()
        }

        SwipeToDismissBoxValue.Settled -> {}
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
                    .background(backgroundColor),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                LottieAnimation(
                    modifier = Modifier
                        .padding(24.dp),
                    composition = composition,
                    iterations = 1,
                    restartOnPlay = false,
                    isPlaying = true,
                )

                LottieAnimation(
                    modifier = Modifier
                        .padding(24.dp),
                    composition = composition,
                    iterations = 1,
                    restartOnPlay = false,
                    isPlaying = true
                )
            }
        }

    ) {
        content()
    }
}

@Preview
@Composable
private fun SwipeItemPreview() {
    MyApplicationTheme {
        SwipeItem(
            model = SwipeItemModel(
                iconAnimated = 0,
                backgroundColor = Color.Red,
                onClick = {},
                content = {
                    FuelStationItem(
                        model = FuelStationItemModel(
                            idServiceStation = 1,
                            icon = R.drawable.ic_logo_repsol,
                            name = "EDAN REPSOL",
                            distance = "567 m",
                            price = "1.75 €/l",
                            index = 3686,
                            categoryColor = Red500,
                            onItemClick = {}
                        )
                    )
                }
            )
        )
    }
}