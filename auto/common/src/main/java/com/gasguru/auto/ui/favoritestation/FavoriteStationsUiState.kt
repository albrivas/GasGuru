package com.gasguru.auto.ui.favoritestation

import com.gasguru.core.model.data.FuelType
import com.gasguru.core.ui.models.FuelStationUiModel

data class FavoriteStationsUiState(
    val loading: Boolean = false,
    val stations: List<FuelStationUiModel> = emptyList(),
    val selectedFuel: FuelType? = null,
)