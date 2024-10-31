package com.gasguru.feature.detail_station.ui

import com.gasguru.core.model.data.FuelStation

sealed interface DetailStationUiState {
    data object Loading : DetailStationUiState
    data class Success(val station: FuelStation) : DetailStationUiState
    data object Error : DetailStationUiState
}