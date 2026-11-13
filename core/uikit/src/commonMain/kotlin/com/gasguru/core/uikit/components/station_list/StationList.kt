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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import com.gasguru.core.uikit.components.fuelItem.FuelStationItem
import com.gasguru.core.uikit.components.fuelItem.FuelStationItemModel
import com.gasguru.core.uikit.components.swipe.SwipeItem
import com.gasguru.core.uikit.components.swipe.SwipeItemModel
import com.gasguru.core.uikit.theme.GasGuruTheme
import com.gasguru.core.uikit.utils.TestTags
import com.gasguru.core.uikit.utils.maestroTestTag
import kotlinx.coroutines.flow.distinctUntilChanged

@Composable
fun StationList(
    stations: List<StationListItemModel>,
    onStationClick: (Int) -> Unit,
    modifier: Modifier = Modifier,
    swipeConfig: StationListSwipeModel? = null,
    testTag: String = "station_list",
    listState: LazyListState = rememberLazyListState(),
) {
    LaunchedEffect(stations) {
        snapshotFlow { listState.layoutInfo.totalItemsCount }
            .distinctUntilChanged()
            .collect {
                if (listState.firstVisibleItemIndex != 0) {
                    listState.animateScrollToItem(0)
                }
            }
    }

    var revealedStationId by remember { mutableStateOf<Int?>(null) }
    LaunchedEffect(listState) {
        snapshotFlow { listState.isScrollInProgress }
            .collect { isScrolling -> if (isScrolling) revealedStationId = null }
    }

    LazyColumn(
        state = listState,
        modifier = modifier
            .clip(RoundedCornerShape(8.dp))
            .border(1.dp, GasGuruTheme.colors.neutral300, RoundedCornerShape(8.dp))
            .background(color = GasGuruTheme.colors.neutralWhite)
            .wrapContentHeight()
            .maestroTestTag(testTag),
    ) {
        itemsIndexed(
            items = stations,
            key = { _, item -> item.idServiceStation },
        ) { index, item ->
            val stationItem = @Composable {
                FuelStationItem(
                    modifier = Modifier
                        .maestroTestTag(TestTags.Home.stationItem(index))
                        .animateItem(),
                    model = FuelStationItemModel(
                        idServiceStation = item.idServiceStation,
                        icon = item.icon,
                        name = item.name,
                        distance = item.distance,
                        price = item.price,
                        index = index,
                        categoryColor = item.categoryColor,
                        onItemClick = onStationClick,
                    ),
                    isLastItem = index == stations.size - 1,
                )
            }

            if (swipeConfig != null) {
                SwipeItem(
                    modifier = Modifier.animateItem(),
                    model = SwipeItemModel(
                        icon = swipeConfig.icon,
                        backgroundColor = swipeConfig.backgroundColor,
                        onClick = {
                            revealedStationId = null
                            swipeConfig.onSwipe(item.idServiceStation)
                        },
                        iconAnimationFile = swipeConfig.iconAnimationFile,
                        isRevealed = revealedStationId == item.idServiceStation,
                        onRevealedChange = { isRevealed ->
                            revealedStationId = when {
                                isRevealed -> item.idServiceStation
                                revealedStationId == item.idServiceStation -> null
                                else -> revealedStationId
                            }
                        },
                    ),
                ) {
                    stationItem()
                }
            } else {
                stationItem()
            }
        }
    }
}
