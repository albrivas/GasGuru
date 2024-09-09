package com.albrivas.fuelpump.feature.fuel_list_station.ui

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.albrivas.fuelpump.core.data.repository.LocationTracker
import com.albrivas.fuelpump.core.domain.FuelStationByLocationUseCase
import com.albrivas.fuelpump.core.domain.GetFavoriteStationsUseCase
import com.albrivas.fuelpump.core.domain.GetUserDataUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.cancelAndJoin
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

private const val SELECTED_TAB = "0"

@HiltViewModel
class FuelListStationViewModel @Inject constructor(
    private val fuelStationByLocation: FuelStationByLocationUseCase,
    private val userLocation: LocationTracker,
    private val getUserDataUseCase: GetUserDataUseCase,
    private val getFavoriteStationsUseCase: GetFavoriteStationsUseCase,
    private val savedStateHandle: SavedStateHandle,
) : ViewModel() {

    private var fetchJob: Job? = null

    val selectedTab = savedStateHandle.getStateFlow(key = SELECTED_TAB, initialValue = "0")

    private val _state = MutableStateFlow<FuelStationListUiState>(FuelStationListUiState.Loading)
    val state: StateFlow<FuelStationListUiState> = _state

    fun updateSelectedFilterIndex(index: Int) {
        savedStateHandle[SELECTED_TAB] = "$index"
        when (index) {
            0 -> checkLocationEnabled()
            1 -> getFavoriteStations()
        }
    }

    private fun getStationsByLocation() {
        viewModelScope.launch {
            fetchJob?.cancel()
            fetchJob = launch {
                userLocation.getCurrentLocation()?.let { location ->
                    combine(
                        fuelStationByLocation(userLocation = location, maxStations = 10),
                        getUserDataUseCase()
                    ) { fuelStations, userData ->
                        Pair(fuelStations, userData)
                    }.onStart { _state.update { FuelStationListUiState.Loading } }
                        .catch { _state.update { FuelStationListUiState.Error } }
                        .collect { (fuelStations, userData) ->
                            _state.update {
                                FuelStationListUiState.Success(
                                    fuelStations = fuelStations,
                                    userSelectedFuelType = userData.fuelSelection
                                )
                            }
                        }
                }
            }
        }
    }

    fun checkLocationEnabled() {
        viewModelScope.launch {
            val isLocationEnabled = userLocation.isLocationEnabled()
            if (!isLocationEnabled) {
                _state.update { FuelStationListUiState.DisableLocation }
            } else {
                getStationsByLocation()
            }
        }
    }

    private fun getFavoriteStations() {
        viewModelScope.launch {
            fetchJob?.cancel()
            fetchJob = launch {
                userLocation.getCurrentLocation()?.let { location ->
                    combine(
                        getFavoriteStationsUseCase(userLocation = location),
                        getUserDataUseCase()
                    ) { stations, userData ->
                        Pair(stations, userData)
                    }.onStart { _state.update { FuelStationListUiState.Loading } }
                        .catch { _state.update { FuelStationListUiState.Error } }
                        .collect { (stations, userData) ->
                            _state.update {
                                if (stations.favoriteStations.isEmpty()) {
                                    FuelStationListUiState.EmptyFavorites
                                } else {
                                    FuelStationListUiState.Favorites(
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

    override fun onCleared() {
        super.onCleared()
        viewModelScope.launch {
            fetchJob?.cancelAndJoin()
        }
    }
}
