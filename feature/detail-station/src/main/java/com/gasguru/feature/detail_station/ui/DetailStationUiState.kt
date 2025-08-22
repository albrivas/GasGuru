package com.gasguru.feature.detail_station.ui

import com.gasguru.core.ui.models.FuelStationUiModel

sealed interface DetailStationUiState {
    data object Loading : DetailStationUiState
    data class Success(val stationModel: FuelStationUiModel, val address: String?) : DetailStationUiState
    data object Error : DetailStationUiState
}
