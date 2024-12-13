package com.gasguru.feature.favorite_list_station.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.gasguru.core.domain.GetFavoriteStationsUseCase
import com.gasguru.core.domain.GetUserDataUseCase
import com.gasguru.core.domain.location.GetLastKnownLocationUseCase
import com.gasguru.core.domain.location.IsLocationEnabledUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class FavoriteListStationViewModel @Inject constructor(
    private val getUserDataUseCase: GetUserDataUseCase,
    private val getFavoriteStationsUseCase: GetFavoriteStationsUseCase,
    private val isLocationEnabledUseCase: IsLocationEnabledUseCase,
    private val getLastKnownLocationUseCase: GetLastKnownLocationUseCase,
) : ViewModel() {

    private val _state =
        MutableStateFlow<FavoriteStationListUiState>(FavoriteStationListUiState.Loading)
    val state: StateFlow<FavoriteStationListUiState> = _state

    init {
        checkLocationEnabled()
    }

    fun checkLocationEnabled() {
        viewModelScope.launch {
            val isLocationEnabled = isLocationEnabledUseCase()
            if (!isLocationEnabled) {
                _state.update { FavoriteStationListUiState.DisableLocation }
            } else {
                getFavoriteStations()
            }
        }
    }

    private fun getFavoriteStations() {
        viewModelScope.launch {
            val lastLocation = getLastKnownLocationUseCase().firstOrNull()
            lastLocation?.let {
                combine(
                    getFavoriteStationsUseCase(userLocation = lastLocation),
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
