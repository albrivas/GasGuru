package com.albrivas.fuelpump.feature.fuel_list_station.ui

import com.albrivas.fuelpump.core.model.data.FuelStation

sealed interface FuelStationListUiState {
    data object Loading : FuelStationListUiState
    data object Error: FuelStationListUiState
    data class Success(val fuelStations: List<FuelStation>): FuelStationListUiState
    data object DisableLocation: FuelStationListUiState
}