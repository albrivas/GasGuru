package com.gasguru.feature.detail_station.ui

sealed interface DetailStationEvent {
    data class ToggleFavorite(val isFavorite: Boolean) : DetailStationEvent
}