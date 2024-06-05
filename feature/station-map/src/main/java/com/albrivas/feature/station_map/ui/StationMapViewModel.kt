package com.albrivas.feature.station_map.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.albrivas.fuelpump.core.common.toLatLng
import com.albrivas.fuelpump.core.data.repository.LocationTracker
import com.albrivas.fuelpump.core.domain.FuelStationByLocationUseCase
import com.albrivas.fuelpump.core.model.data.FuelStation
import com.google.android.gms.maps.model.LatLng
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class StationMapViewModel @Inject constructor(
    private val fuelStationByLocation: FuelStationByLocationUseCase,
    private val userLocation: LocationTracker,
) : ViewModel() {

    companion object {
        private const val DEFAULT_LATITUDE = 40.4165
        private const val DEFAULT_LONGITUDE = -3.70256
    }

    private val _state = MutableStateFlow(StationMapUiState())
    val state: StateFlow<StationMapUiState> = _state

    fun getStationsByLocation() {
        viewModelScope.launch {
            userLocation.getCurrentLocation()?.let { location ->
                fuelStationByLocation(userLocation = location, maxStations = 30)
                    .catch { error -> _state.update { it.copy(error = error) } }
                    .collect { fuelStations ->
                        _state.update {
                            it.copy(
                                fuelStations = fuelStations,
                                centerMap = location.toLatLng(),
                                zoomLevel = 14f
                            )
                        }
                    }
            }
        }
    }

    data class StationMapUiState(
        val fuelStations: List<FuelStation> = emptyList(),
        val centerMap: LatLng = LatLng(DEFAULT_LATITUDE, DEFAULT_LONGITUDE),
        val zoomLevel: Float = 1f,
        val error: Throwable? = null
    )
}