package com.albrivas.feature.station_map.ui

import com.albrivas.fuelpump.core.model.data.FuelStation
import com.albrivas.fuelpump.core.model.data.FuelType
import com.google.android.gms.maps.model.LatLng

data class StationMapUiState(
    val fuelStations: List<FuelStation> = emptyList(),
    val centerMap: LatLng = LatLng(0.0, 0.0),
    val zoomLevel: Float = 12f,
    val error: Throwable? = null,
    val selectedType: FuelType? = null,
)
