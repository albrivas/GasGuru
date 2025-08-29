package com.gasguru.core.uikit.components.filterable_station_list

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.gasguru.core.uikit.components.segmented.HeaderSegmentedTabs
import com.gasguru.core.uikit.components.segmented.HeaderSegmentedTabsModel
import com.gasguru.core.uikit.components.station_list.StationList

@Composable
fun FilterableStationList(
    model: FilterableStationListModel,
    modifier: Modifier = Modifier
) {
    val listState = rememberLazyListState()
    
    Column(
        modifier = modifier
            .fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        HeaderSegmentedTabs(
            modifier = Modifier.fillMaxWidth(),
            model = HeaderSegmentedTabsModel(
                tabs = listOf("Price", "Distance"),
                selectedTab = model.selectedTab,
                onSelectedTab = model.onTabChange
            )
        )

        StationList(
            stations = model.stations,
            onStationClick = model.onStationClick,
            swipeConfig = model.swipeConfig,
            testTag = model.testTag ?: "station_list",
            listState = listState
        )
    }
}
