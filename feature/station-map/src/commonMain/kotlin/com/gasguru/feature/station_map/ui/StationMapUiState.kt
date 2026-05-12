package com.gasguru.feature.station_map.ui

import com.gasguru.core.model.data.FuelType
import com.gasguru.core.model.data.LatLng
import com.gasguru.core.ui.models.FuelStationUiModel
import com.gasguru.feature.station_map.ui.model.GeoBounds
import com.gasguru.feature.station_map.ui.models.RouteUiModel

data class StationMapUiState(
    val mapStations: List<FuelStationUiModel> = emptyList(),
    val listStations: List<FuelStationUiModel> = emptyList(),
    val error: Throwable? = null,
    val selectedType: FuelType? = null,
    val showListStations: Boolean = false,
    val loading: Boolean = true,
    val mapBounds: GeoBounds? = null,
    val shouldCenterMap: Boolean = false,
    val startRoute: Boolean = false,
    val route: RouteUiModel? = null,
    val routeDestinationName: String? = null,
    val userLocationToCenter: LatLng? = null,
    val selectedStationId: Int = -1,
)

data class SelectedTabUiState(
    val selectedTab: StationSortTab = StationSortTab.PRICE,
)

enum class StationSortTab(val value: Int) {
    PRICE(0),
    DISTANCE(1);

    companion object {
        fun fromValue(value: Int): StationSortTab =
            entries.find { it.value == value } ?: PRICE
    }
}
