package com.gasguru.feature.favorite_list_station.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.gasguru.core.domain.fuelstation.GetFavoriteStationsUseCase
import com.gasguru.core.domain.fuelstation.RemoveFavoriteStationUseCase
import com.gasguru.core.domain.location.GetLastKnownLocationUseCase
import com.gasguru.core.domain.location.IsLocationEnabledUseCase
import com.gasguru.core.domain.user.GetUserDataUseCase
import com.gasguru.core.ui.toUiModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
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

    fun handleEvents(event: FavoriteStationEvent) {
        when (event) {
            is FavoriteStationEvent.RemoveFavoriteStation -> removeFavoriteStation(event.idStation)
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
                            getUserDataUseCase()
                        ) { stations, userData ->
                            if (stations.favoriteStations.isEmpty()) {
                                FavoriteStationListUiState.EmptyFavorites
                            } else {
                                FavoriteStationListUiState.Favorites(
                                    favoriteStations = stations.favoriteStations.map { it.toUiModel() },
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
}
