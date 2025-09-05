package com.gasguru.auto.ui.nearbystation

import com.gasguru.core.model.data.FuelType
import com.gasguru.core.ui.models.FuelStationUiModel

data class NearbyStationsUiState(
    val loading: Boolean = false,
    val stations: List<FuelStationUiModel> = emptyList(),
    val selectedFuel: FuelType? = null,
    val locationDisabled: Boolean = false,
)