package com.gasguru.core.uikit.components.station_list

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import com.gasguru.core.uikit.components.fuelItem.FuelStationItem
import com.gasguru.core.uikit.components.fuelItem.FuelStationItemModel
import com.gasguru.core.uikit.components.swipe.SwipeItem
import com.gasguru.core.uikit.components.swipe.SwipeItemModel
import com.gasguru.core.uikit.theme.GasGuruTheme
import kotlinx.coroutines.flow.distinctUntilChanged

@Composable
fun StationList(
    stations: List<StationListItemModel>,
    onStationClick: (Int) -> Unit,
    modifier: Modifier = Modifier,
    swipeConfig: StationListSwipeModel? = null,
    testTag: String = "station_list",
    listState: LazyListState = rememberLazyListState()
) {
    // Wait until animatedItem finished, to go to the first item of the list
    LaunchedEffect(stations) {
        snapshotFlow { listState.layoutInfo.totalItemsCount }
            .distinctUntilChanged()
            .collect {
                if (listState.firstVisibleItemIndex != 0) {
                    listState.animateScrollToItem(0)
                }
            }
    }

    LazyColumn(
        state = listState,
        modifier = modifier
            .clip(RoundedCornerShape(8.dp))
            .border(1.dp, GasGuruTheme.colors.neutral300, RoundedCornerShape(8.dp))
            .background(color = GasGuruTheme.colors.neutralWhite)
            .wrapContentHeight()
            .testTag(testTag)
    ) {
        itemsIndexed(
            items = stations,
            key = { _, item -> item.idServiceStation }
        ) { index, item ->
            val stationItem = @Composable {
                FuelStationItem(
                    modifier = Modifier
                        .testTag("item $index")
                        .animateItem(),
                    model = FuelStationItemModel(
                        idServiceStation = item.idServiceStation,
                        icon = item.icon,
                        name = item.name,
                        distance = item.distance,
                        price = item.price,
                        index = index,
                        categoryColor = item.categoryColor,
                        onItemClick = onStationClick
                    ),
                    isLastItem = index == stations.size - 1
                )
            }

            if (swipeConfig != null) {
                SwipeItem(
                    modifier = Modifier.animateItem(),
                    model = SwipeItemModel(
                        enableDismissFromEndToStart = true,
                        enableDismissFromStartToEnd = true,
                        iconAnimated = swipeConfig.iconAnimated,
                        backgroundColor = swipeConfig.backgroundColor,
                        onClick = { swipeConfig.onSwipe(item.idServiceStation) }
                    )
                ) {
                    stationItem()
                }
            } else {
                stationItem()
            }
        }
    }
}
