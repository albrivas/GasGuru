package com.albrivas.fuelpump.feature.fuel_list_station.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.albrivas.fuelpump.core.data.repository.LocationTracker
import com.albrivas.fuelpump.core.domain.FuelStationByLocationUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class FuelListStationViewModel @Inject constructor(
    private val fuelStationByLocation: FuelStationByLocationUseCase,
    private val userLocation: LocationTracker,
) : ViewModel() {

    private val _state = MutableStateFlow<FuelStationListUiState>(FuelStationListUiState.Loading)
    val state: StateFlow<FuelStationListUiState> = _state

    init {
        checkLocationEnabled()
    }

    private fun getStationsByLocation() {
        viewModelScope.launch {
            userLocation.getCurrentLocation()?.let { location ->
                fuelStationByLocation(location, 50)
                    .catch { _state.update { FuelStationListUiState.Error } }
                    .collect { fuelStations ->
                        _state.update { FuelStationListUiState.Success(fuelStations) }
                    }
            }
        }
    }

    private fun checkLocationEnabled() {
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
