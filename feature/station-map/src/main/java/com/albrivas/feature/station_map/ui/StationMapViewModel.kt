package com.albrivas.feature.station_map.ui

import android.location.Location
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.albrivas.fuelpump.core.common.toLatLng
import com.albrivas.fuelpump.core.common.toLocation
import com.albrivas.fuelpump.core.data.repository.LocationTracker
import com.albrivas.fuelpump.core.domain.FuelStationByLocationUseCase
import com.albrivas.fuelpump.core.domain.GetUserDataUseCase
import com.google.android.gms.maps.model.LatLng
import com.google.android.libraries.places.api.net.FetchPlaceRequest
import com.google.android.libraries.places.api.net.FindAutocompletePredictionsRequest
import com.google.android.libraries.places.api.net.PlacesClient
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

private const val SEARCH_QUERY = "searchQuery"

@HiltViewModel
class StationMapViewModel @Inject constructor(
    private val fuelStationByLocation: FuelStationByLocationUseCase,
    private val userLocation: LocationTracker,
    private val getUserDataUseCase: GetUserDataUseCase,
    private val placesClient: PlacesClient,
    private val savedStateHandle: SavedStateHandle
) : ViewModel() {

    val searchQuery = savedStateHandle.getStateFlow(key = SEARCH_QUERY, initialValue = "")

    private val _state = MutableStateFlow(StationMapUiState())
    val state: StateFlow<StationMapUiState> = _state

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

    @OptIn(ExperimentalCoroutinesApi::class)
    val searchResultUiState: StateFlow<SearchResultUiState> =
        searchQuery.flatMapLatest { query ->
            if (query.length < 2) {
                flowOf(SearchResultUiState.EmptyQuery)
            } else {
                try {
                    val request =
                        FindAutocompletePredictionsRequest.builder().setQuery(query)
                            .setCountries("ES")
                            .build()
                    val result = placesClient.findAutocompletePredictions(request).await()
                    val predictions = result.autocompletePredictions.map {
                        SearchPlace(it.getFullText(null).toString(), it.placeId)
                    }
                    if (predictions.isEmpty())
                        flowOf(SearchResultUiState.EmptySearchResult)
                    else
                        flowOf(SearchResultUiState.Success(predictions))
                }catch (e: Exception){
                    flowOf(SearchResultUiState.EmptySearchResult)
                }
            }
        }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = SearchResultUiState.Loading,
        )

    fun getStationByPlace(placeId: String) {
        viewModelScope.launch {
            getLatLngPlace(placeId)?.let {
                getStationByLocation(it.toLocation())
            }
        }
    }

    private suspend fun getLatLngPlace(placeId: String): LatLng? {
        val placeFields = listOf(
            com.google.android.libraries.places.api.model.Place.Field.ID,
            com.google.android.libraries.places.api.model.Place.Field.NAME,
            com.google.android.libraries.places.api.model.Place.Field.LAT_LNG
        )
        val request = FetchPlaceRequest.newInstance(placeId, placeFields)
        val response = placesClient.fetchPlace(request).await()
        return response.place.latLng
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