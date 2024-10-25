package com.albrivas.fuelpump.feature.favorite_list_station.ui

import com.albrivas.fuelpump.core.model.data.FuelStation
import com.albrivas.fuelpump.core.model.data.FuelType

sealed interface FavoriteStationListUiState {
    data object Loading : FavoriteStationListUiState
    data object Error : FavoriteStationListUiState
    data object DisableLocation : FavoriteStationListUiState
    data class Favorites(val favoriteStations: List<FuelStation>, val userSelectedFuelType: FuelType) :
        FavoriteStationListUiState
    data object EmptyFavorites : FavoriteStationListUiState
}
