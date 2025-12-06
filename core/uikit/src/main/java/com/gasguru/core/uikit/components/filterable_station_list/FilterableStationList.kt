package com.gasguru.core.uikit.components.filterable_station_list

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.gasguru.core.uikit.components.segmented.HeaderFilterTabs
import com.gasguru.core.uikit.components.segmented.HeaderFilterTabsModel
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
        HeaderFilterTabs(
            modifier = Modifier.fillMaxWidth(),
            model = HeaderFilterTabsModel(
                tabs = model.tabNames,
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
