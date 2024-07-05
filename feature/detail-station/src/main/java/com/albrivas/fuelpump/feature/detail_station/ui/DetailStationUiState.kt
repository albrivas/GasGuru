package com.albrivas.fuelpump.feature.detail_station.ui

import com.albrivas.fuelpump.core.model.data.FuelStation

sealed interface DetailStationUiState {
    data object Loading : DetailStationUiState
    data class Success(val station: FuelStation) : DetailStationUiState
    data object Error : DetailStationUiState
}
