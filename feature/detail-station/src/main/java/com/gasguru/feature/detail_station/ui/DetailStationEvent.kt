package com.gasguru.feature.detail_station.ui

sealed interface DetailStationEvent {
    data class ToggleFavorite(val isFavorite: Boolean) : DetailStationEvent
    data class TogglePriceAlert(val isEnabled: Boolean) : DetailStationEvent
    data class UpdateTankCapacity(val capacity: Int) : DetailStationEvent
    data object ShareStation : DetailStationEvent
}
