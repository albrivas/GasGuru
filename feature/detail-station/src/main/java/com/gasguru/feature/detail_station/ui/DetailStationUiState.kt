package com.gasguru.feature.detail_station.ui

import com.gasguru.core.model.data.FuelStation
import com.gasguru.core.model.data.PriceHistory

sealed interface DetailStationUiState {
    data object Loading : DetailStationUiState
    data class Success(val station: FuelStation, val address: String?) : DetailStationUiState
    data object Error : DetailStationUiState
}

sealed interface PriceHistoryUiState {
    data class Success(val prices: List<PriceHistory>) : PriceHistoryUiState
    data object Loading : PriceHistoryUiState
    data object Error : PriceHistoryUiState
}
