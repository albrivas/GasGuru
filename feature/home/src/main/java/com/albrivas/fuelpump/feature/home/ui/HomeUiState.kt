package com.albrivas.fuelpump.feature.home.ui

import com.albrivas.fuelpump.core.model.data.FuelStation

sealed interface HomeUiState {
    data object Loading : HomeUiState
    data object Error: HomeUiState
    data class Success(val fuelStations: List<FuelStation>): HomeUiState
    data object DisableLocation: HomeUiState
}