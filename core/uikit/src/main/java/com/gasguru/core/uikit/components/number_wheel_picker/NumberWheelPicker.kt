package com.gasguru.core.uikit.components.number_wheel_picker

import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.snapping.rememberSnapFlingBehavior
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.gasguru.core.uikit.theme.GasGuruTheme
import com.gasguru.core.uikit.theme.MyApplicationTheme
import com.gasguru.core.uikit.theme.ThemePreviews

@Composable
fun NumberWheelPicker(
    model: NumberWheelPickerModel,
    modifier: Modifier = Modifier,
) = with(model) {
    val itemHeightDp: Dp = 52.dp
    val values = (min..max).toList()
    val initialIndex = (initialValue - min).coerceIn(values.indices)

    val listState = rememberLazyListState(
        initialFirstVisibleItemIndex = initialIndex,
    )
    val flingBehavior = rememberSnapFlingBehavior(lazyListState = listState)

    // With a leading Spacer at list index 0, firstVisibleItemIndex == values index of centered item:
    // FVI=0 → Spacer at top, values[0] at center
    // FVI=k → values[k-1] at top, values[k] at center
    val centeredIndex by remember {
        derivedStateOf { listState.firstVisibleItemIndex.coerceIn(values.indices) }
    }

    LaunchedEffect(key1 = centeredIndex) {
        onValueChanged(values[centeredIndex])
    }

    Box(modifier = modifier) {
        LazyColumn(
            state = listState,
            flingBehavior = flingBehavior,
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.fillMaxSize(),
        ) {
            item { Box(modifier = Modifier.height(itemHeightDp)) }
            items(count = values.size) { index ->
                val isSelected = index == centeredIndex
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(itemHeightDp),
                ) {
                    Text(
                        text = values[index].toString(),
                        fontSize = if (isSelected) 28.sp else 18.sp,
                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                        color = if (isSelected) {
                            GasGuruTheme.colors.primary500
                        } else {
                            GasGuruTheme.colors.neutral600
                        },
                    )
                }
            }
            item { Box(modifier = Modifier.height(itemHeightDp)) }
        }

        // Selection highlight band
        Box(
            modifier = Modifier
                .align(Alignment.Center)
                .fillMaxWidth()
                .padding(horizontal = 16.dp)
                .height(itemHeightDp)
                .background(
                    color = GasGuruTheme.colors.primary100.copy(alpha = 0.4f),
                    shape = RoundedCornerShape(size = 8.dp),
                ),
        )

        // Top fade gradient
        Box(
            modifier = Modifier
                .align(Alignment.TopCenter)
                .fillMaxWidth()
                .height(itemHeightDp)
                .background(
                    brush = Brush.verticalGradient(
                        colors = listOf(
                            GasGuruTheme.colors.neutralWhite,
                            Color.Transparent,
                        ),
                    ),
                ),
        )

        // Bottom fade gradient
        Box(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
                .height(itemHeightDp)
                .background(
                    brush = Brush.verticalGradient(
                        colors = listOf(
                            Color.Transparent,
                            GasGuruTheme.colors.neutralWhite,
                        ),
                    ),
                ),
        )
    }
}

@Composable
@ThemePreviews
private fun NumberWheelPickerPreview() {
    MyApplicationTheme {
        NumberWheelPicker(
            model = NumberWheelPickerModel(
                min = 40,
                max = 999,
                initialValue = 60,
                onValueChanged = {},
            ),
            modifier = Modifier
                .height(156.dp)
                .fillMaxWidth(),
        )
    }
}