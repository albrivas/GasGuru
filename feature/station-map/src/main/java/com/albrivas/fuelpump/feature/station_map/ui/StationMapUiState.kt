package com.albrivas.fuelpump.feature.station_map.ui

import com.albrivas.fuelpump.core.model.data.FuelStation
import com.albrivas.fuelpump.core.model.data.FuelType
import com.google.android.gms.maps.model.LatLng

data class StationMapUiState(
    val fuelStations: List<FuelStation> = emptyList(),
    val centerMap: LatLng? = null,
    val zoomLevel: Float = 14f,
    val error: Throwable? = null,
    val selectedType: FuelType? = null,
)
