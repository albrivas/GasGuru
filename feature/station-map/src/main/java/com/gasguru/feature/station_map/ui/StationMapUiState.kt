package com.gasguru.feature.station_map.ui

import com.gasguru.core.model.data.FuelType
import com.gasguru.core.model.data.Route
import com.gasguru.core.ui.models.FuelStationUiModel
import com.google.android.gms.maps.model.LatLngBounds

data class StationMapUiState(
    val mapStations: List<FuelStationUiModel> = emptyList(), // Only for map
    val listStations: List<FuelStationUiModel> = emptyList(), // Only for sheet
    val error: Throwable? = null,
    val selectedType: FuelType? = null,
    val showListStations: Boolean = false,
    val loading: Boolean = false,
    val mapBounds: LatLngBounds? = null,
    val shouldCenterMap: Boolean = false,
    val startRoute: Boolean = false,
    val route: Route? = null,
    val routeDestinationName: String? = null,
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
