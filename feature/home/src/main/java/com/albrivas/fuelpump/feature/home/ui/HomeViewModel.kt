package com.albrivas.fuelpump.feature.home.ui

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.albrivas.fuelpump.core.data.repository.LocationTracker
import com.albrivas.fuelpump.core.domain.FuelStationByLocationUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val fuelStationByLocation: FuelStationByLocationUseCase,
    private val userLocation: LocationTracker,
) : ViewModel() {

    var state by mutableStateOf<HomeUiState>(HomeUiState.Loading)
        private set

    init {
        getStationsByLocation()
    }

    private fun getStationsByLocation() {
        viewModelScope.launch {
            userLocation.getCurrentLocation()?.let { location ->
                fuelStationByLocation(location)
                    .catch { state = HomeUiState.Error }
                    .collect { fuelStations ->
                        state = HomeUiState.Success(fuelStations)
                    }
            }
        }
    }
}
