package com.gasguru.feature.station_map.ui

import com.gasguru.core.model.data.FuelType
import com.gasguru.core.model.data.Route
import com.gasguru.core.ui.models.FuelStationUiModel
import com.google.android.gms.maps.model.LatLngBounds

data class StationMapUiState(
    val fuelStations: List<FuelStationUiModel> = emptyList(),
    val error: Throwable? = null,
    val selectedType: FuelType? = null,
    val showListStations: Boolean = false,
    val loading: Boolean = false,
    val mapBounds: LatLngBounds? = null,
    val shouldCenterMap: Boolean = false,
    val startRoute: Boolean = false,
    val route: Route? = null,
)

data class SelectedTabUiState(
    val selectedTab: Int = 0,
)
