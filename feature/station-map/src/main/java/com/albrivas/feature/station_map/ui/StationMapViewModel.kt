package com.albrivas.feature.station_map.ui

import android.location.Location
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.albrivas.fuelpump.core.common.toLatLng
import com.albrivas.fuelpump.core.data.repository.LocationTracker
import com.albrivas.fuelpump.core.domain.FuelStationByLocationUseCase
import com.albrivas.fuelpump.core.domain.GetLocationPlaceUseCase
import com.albrivas.fuelpump.core.domain.GetPlacesUseCase
import com.albrivas.fuelpump.core.domain.GetUserDataUseCase
import com.albrivas.fuelpump.core.model.data.SearchPlace
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

private const val SEARCH_QUERY = "searchQuery"
private const val SEARCH_QUERY_MIN_LENGTH = 2

@HiltViewModel
class StationMapViewModel @Inject constructor(
    private val fuelStationByLocation: FuelStationByLocationUseCase,
    private val userLocation: LocationTracker,
    private val getUserDataUseCase: GetUserDataUseCase,
    private val savedStateHandle: SavedStateHandle,
    private val getPlacesUseCase: GetPlacesUseCase,
    private val getLocationPlaceUseCase: GetLocationPlaceUseCase,
) : ViewModel() {

    val searchQuery = savedStateHandle.getStateFlow(key = SEARCH_QUERY, initialValue = "")

    private val _state = MutableStateFlow(StationMapUiState())
    val state: StateFlow<StationMapUiState> = _state

    @OptIn(ExperimentalCoroutinesApi::class)
    val searchResultUiState: StateFlow<SearchResultUiState> =
        searchQuery.flatMapLatest { query ->
            if (query.length < SEARCH_QUERY_MIN_LENGTH) {
                flowOf(SearchResultUiState.EmptyQuery)
            } else {
                getPlacesUseCase(query).map { predictions ->
                    if(predictions.isEmpty())
                        SearchResultUiState.EmptySearchResult
                    else
                        SearchResultUiState.Success(predictions)
                }
            }
        }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = SearchResultUiState.Loading,
        )

    fun onSearchQueryChanged(query: String) {
        savedStateHandle[SEARCH_QUERY] = query
    }

    fun getStationByCurrentLocation() {
        viewModelScope.launch {
            userLocation.getCurrentLocation()?.let { location ->
                getStationByLocation(location)
            }
        }
    }

    fun getStationByPlace(placeId: String) =
        viewModelScope.launch {
            getLocationPlaceUseCase(placeId).collect { location ->
                getStationByLocation(location)
            }
        }


    private fun getStationByLocation(location: Location) {
        viewModelScope.launch {
            combine(
                fuelStationByLocation(userLocation = location, maxStations = 30),
                getUserDataUseCase()
            ) { fuelStations, userData ->
                Pair(fuelStations, userData)
            }.catch { error ->
                _state.update { it.copy(error = error) }
            }.collect { (fuelStations, userData) ->
                _state.update {
                    it.copy(
                        fuelStations = fuelStations,
                        centerMap = location.toLatLng(),
                        zoomLevel = 14f,
                        selectedType = userData.fuelSelection
                    )
                }
            }
        }
    }
}