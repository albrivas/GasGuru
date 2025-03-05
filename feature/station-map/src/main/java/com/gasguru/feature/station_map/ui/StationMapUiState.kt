package com.gasguru.feature.station_map.ui

import com.gasguru.core.model.data.FuelStation
import com.gasguru.core.model.data.FuelType
import com.google.android.gms.maps.model.LatLngBounds

data class StationMapUiState(
    val fuelStations: List<FuelStation> = emptyList(),
    val error: Throwable? = null,
    val selectedType: FuelType? = null,
    val showListStations: Boolean = false,
    val loading: Boolean = false,
    val mapBounds: LatLngBounds? = null,
)
