package com.albrivas.fuelpump.feature.home.ui

import android.location.Location
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.albrivas.fuelpump.core.data.repository.LocationTracker
import com.albrivas.fuelpump.core.domain.FuelStationByLocationUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.single
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val fuelStationByLocation: FuelStationByLocationUseCase,
    private val userLocation: LocationTracker,
) : ViewModel() {

    private val _state = MutableStateFlow<HomeUiState>(HomeUiState.Loading)
    val state: StateFlow<HomeUiState> = _state

    init {
        checkLocationEnabled()
    }

    private fun getStationsByLocation() {
        viewModelScope.launch {
            userLocation.getCurrentLocation()?.let { location ->
                fuelStationByLocation(location)
                    .catch { _state.update { HomeUiState.Error } }
                    .collect { fuelStations ->
                        _state.update { HomeUiState.Success(fuelStations) }
                    }
            }
        }
    }

    private fun checkLocationEnabled() {
        viewModelScope.launch {
            val isLocationEnabled = userLocation.isLocationEnabled()
            if (!isLocationEnabled) {
                _state.update { HomeUiState.DisableLocation }
            } else {
                getStationsByLocation()
            }
        }
    }
}
