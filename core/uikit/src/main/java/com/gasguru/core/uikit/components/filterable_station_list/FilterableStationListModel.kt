package com.gasguru.core.uikit.components.filterable_station_list

import com.gasguru.core.uikit.components.station_list.StationListItemModel
import com.gasguru.core.uikit.components.station_list.StationListSwipeModel

data class FilterableStationListModel(
    val stations: List<StationListItemModel>,
    val selectedTab: Int,
    val onTabChange: (Int) -> Unit,
    val onStationClick: (Int) -> Unit,
    val swipeConfig: StationListSwipeModel? = null,
    val testTag: String? = null,
    val tabNames: List<String> = emptyList()
)
