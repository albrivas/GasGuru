package com.albrivas.fuelpump.feature.fuel_list_station.ui

import com.albrivas.fuelpump.core.model.data.FuelStation
import com.albrivas.fuelpump.core.model.data.FuelType

sealed interface FuelStationListUiState {
    data object Loading : FuelStationListUiState
    data object Error : FuelStationListUiState
    data object DisableLocation : FuelStationListUiState
    data class Favorites(val favoriteStations: List<FuelStation>, val userSelectedFuelType: FuelType) :
        FuelStationListUiState
    data object EmptyFavorites : FuelStationListUiState
}
