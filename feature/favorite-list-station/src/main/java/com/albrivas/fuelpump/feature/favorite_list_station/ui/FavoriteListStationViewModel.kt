package com.albrivas.fuelpump.feature.favorite_list_station.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.albrivas.fuelpump.core.data.repository.LocationTracker
import com.albrivas.fuelpump.core.domain.GetFavoriteStationsUseCase
import com.albrivas.fuelpump.core.domain.GetUserDataUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class FavoriteListStationViewModel @Inject constructor(
    private val userLocation: LocationTracker,
    private val getUserDataUseCase: GetUserDataUseCase,
    private val getFavoriteStationsUseCase: GetFavoriteStationsUseCase,
) : ViewModel() {

    private val _state = MutableStateFlow<FavoriteStationListUiState>(FavoriteStationListUiState.Loading)
    val state: StateFlow<FavoriteStationListUiState> = _state

    init {
        checkLocationEnabled()
    }

    fun checkLocationEnabled() {
        viewModelScope.launch {
            val isLocationEnabled = userLocation.isLocationEnabled()
            if (!isLocationEnabled) {
                _state.update { FavoriteStationListUiState.DisableLocation }
            } else {
                getFavoriteStations()
            }
        }
    }

    private fun getFavoriteStations() {
        viewModelScope.launch {
            userLocation.getCurrentLocation()?.let { location ->
                combine(
                    getFavoriteStationsUseCase(userLocation = location),
                    getUserDataUseCase()
                ) { stations, userData ->
                    Pair(stations, userData)
                }.onStart { _state.update { FavoriteStationListUiState.Loading } }
                    .catch { _state.update { FavoriteStationListUiState.Error } }
                    .collect { (stations, userData) ->
                        _state.update {
                            if (stations.favoriteStations.isEmpty()) {
                                FavoriteStationListUiState.EmptyFavorites
                            } else {
                                FavoriteStationListUiState.Favorites(
                                    favoriteStations = stations.favoriteStations,
                                    userSelectedFuelType = userData.fuelSelection
                                )
                            }
                        }
                    }
            }
        }
    }
}
