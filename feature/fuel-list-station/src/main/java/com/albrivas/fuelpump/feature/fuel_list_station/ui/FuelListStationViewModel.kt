package com.albrivas.fuelpump.feature.fuel_list_station.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.albrivas.fuelpump.core.data.repository.LocationTracker
import com.albrivas.fuelpump.core.domain.FuelStationByLocationUseCase
import com.albrivas.fuelpump.core.domain.GetUserDataUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class FuelListStationViewModel @Inject constructor(
    private val fuelStationByLocation: FuelStationByLocationUseCase,
    private val userLocation: LocationTracker,
    private val getUserDataUseCase: GetUserDataUseCase
) : ViewModel() {

    private val _state = MutableStateFlow<FuelStationListUiState>(FuelStationListUiState.Loading)
    val state: StateFlow<FuelStationListUiState> = _state

    init {
        checkLocationEnabled()
    }

    private fun getStationsByLocation() {
        viewModelScope.launch {
            userLocation.getCurrentLocation()?.let { location ->
                combine(
                    fuelStationByLocation(userLocation = location, maxStations = 30),
                    getUserDataUseCase()
                ) { fuelStations, userData ->
                    Pair(fuelStations, userData)
                }.catch { _state.update { FuelStationListUiState.Error } }
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
}
