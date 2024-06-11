package com.albrivas.feature.station_map.ui

import com.albrivas.fuelpump.core.model.data.FuelStation
import com.albrivas.fuelpump.core.model.data.FuelType
import com.google.android.gms.maps.model.LatLng

data class StationMapUiState(
    val fuelStations: List<FuelStation> = emptyList(),
    val centerMap: LatLng = LatLng(40.4165, -3.70256),
    val zoomLevel: Float = 1f,
    val error: Throwable? = null,
    val selectedType: FuelType? = null,
    val suggestionList: List<SearchPlace> = emptyList(),
    val querySearch: String = "",
)
