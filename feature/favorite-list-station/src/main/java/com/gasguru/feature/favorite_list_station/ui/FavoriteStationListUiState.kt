package com.gasguru.feature.favorite_list_station.ui

import com.gasguru.core.model.data.FuelType
import com.gasguru.core.ui.models.FuelStationUiModel

sealed interface FavoriteStationListUiState {
    data object Loading : FavoriteStationListUiState
    data object Error : FavoriteStationListUiState
    data object DisableLocation : FavoriteStationListUiState
    data class Favorites(
        val favoriteStations: List<FuelStationUiModel>,
        val userSelectedFuelType: FuelType,
    ) :
        FavoriteStationListUiState

    data object EmptyFavorites : FavoriteStationListUiState
}

data class SelectedTabUiState(
    val selectedTab: Int = 0,
)

sealed class FavoriteStationEvent {
    data class RemoveFavoriteStation(val idStation: Int) : FavoriteStationEvent()
    data class ChangeTab(val selected: Int) : FavoriteStationEvent()
}
