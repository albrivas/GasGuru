package com.gasguru.feature.favorite_list_station.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.gasguru.core.domain.fuelstation.GetFavoriteStationsUseCase
import com.gasguru.core.domain.fuelstation.RemoveFavoriteStationUseCase
import com.gasguru.core.domain.location.GetLastKnownLocationUseCase
import com.gasguru.core.domain.location.IsLocationEnabledUseCase
import com.gasguru.core.domain.user.GetUserDataUseCase
import com.gasguru.core.ui.mapper.toUiModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class FavoriteListStationViewModel @Inject constructor(
    private val getUserDataUseCase: GetUserDataUseCase,
    private val getFavoriteStationsUseCase: GetFavoriteStationsUseCase,
    isLocationEnabledUseCase: IsLocationEnabledUseCase,
    getLastKnownLocationUseCase: GetLastKnownLocationUseCase,
    private val removeFavoriteStationUseCase: RemoveFavoriteStationUseCase,
) : ViewModel() {

    private val _tabState = MutableStateFlow(SelectedTabUiState())
    val tabState = _tabState.asStateFlow()

    fun handleEvents(event: FavoriteStationEvent) {
        when (event) {
            is FavoriteStationEvent.RemoveFavoriteStation -> removeFavoriteStation(idStation = event.idStation)
            is FavoriteStationEvent.ChangeTab -> changeTab(position = event.selected)
        }
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    val favoriteStations: StateFlow<FavoriteStationListUiState> = isLocationEnabledUseCase()
        .flatMapLatest { isLocationEnabled ->
            if (!isLocationEnabled) {
                flowOf(FavoriteStationListUiState.DisableLocation)
            } else {
                getLastKnownLocationUseCase()
                    .map { location ->
                        location ?: throw IllegalStateException("Location is null")
                    }
                    .flatMapLatest { location ->
                        combine(
                            getFavoriteStationsUseCase(userLocation = location),
                            getUserDataUseCase(),
                            _tabState,
                        ) { stations, userData, tabState ->
                            if (stations.favoriteStations.isEmpty()) {
                                FavoriteStationListUiState.EmptyFavorites
                            } else {
                                val listUiModel = stations.favoriteStations.map { it.toUiModel() }
                                val sortedStations = when (tabState.selectedTab) {
                                    0 -> listUiModel.sortedBy { userData.fuelSelection.extractPrice(it.fuelStation) }
                                    1 -> listUiModel.sortedBy { it.fuelStation.distance }
                                    else -> listUiModel
                                }
                                FavoriteStationListUiState.Favorites(
                                    favoriteStations = sortedStations,
                                    userSelectedFuelType = userData.fuelSelection
                                )
                            }
                        }
                    }
            }
        }
        .catch {
            emit(FavoriteStationListUiState.Error)
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = FavoriteStationListUiState.Loading
        )

    private fun removeFavoriteStation(idStation: Int) = viewModelScope.launch {
        removeFavoriteStationUseCase.invoke(idStation)
    }

    private fun changeTab(position: Int) = _tabState.update { it.copy(selectedTab = position) }
}
