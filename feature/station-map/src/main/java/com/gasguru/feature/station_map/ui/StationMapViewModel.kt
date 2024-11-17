package com.gasguru.feature.station_map.ui

import android.location.Location
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.gasguru.core.common.toLatLng
import com.gasguru.core.data.repository.LocationTracker
import com.gasguru.core.domain.ClearRecentSearchQueriesUseCase
import com.gasguru.core.domain.FuelStationByLocationUseCase
import com.gasguru.core.domain.GetLocationPlaceUseCase
import com.gasguru.core.domain.GetPlacesUseCase
import com.gasguru.core.domain.GetRecentSearchQueryUseCase
import com.gasguru.core.domain.GetUserDataUseCase
import com.gasguru.core.domain.InsertRecentSearchQueryUseCase
import com.gasguru.core.model.data.FuelStationBrandsType
import com.gasguru.core.model.data.SearchPlace
import com.google.android.gms.maps.model.LatLngBounds
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
    private val clearRecentSearchQueriesUseCase: ClearRecentSearchQueriesUseCase,
    private val insertRecentSearchQueryUseCase: InsertRecentSearchQueryUseCase,
    getRecentSearchQueryUseCase: GetRecentSearchQueryUseCase,
) : ViewModel() {

    val searchQuery = savedStateHandle.getStateFlow(key = SEARCH_QUERY, initialValue = "")

    private val _state = MutableStateFlow(StationMapUiState())
    val state: StateFlow<StationMapUiState> = _state

    private val _filterState = MutableStateFlow(FilterUiState())
    val filterState: StateFlow<FilterUiState> = _filterState

    init {
        getStationByCurrentLocation()
        getFilters()
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    val searchResultUiState: StateFlow<SearchResultUiState> =
        searchQuery.flatMapLatest { query ->
            if (query.length < SEARCH_QUERY_MIN_LENGTH) {
                flowOf(SearchResultUiState.EmptyQuery)
            } else {
                getPlacesUseCase(query).map { predictions ->
                    if (predictions.isEmpty()) {
                        SearchResultUiState.EmptySearchResult
                    } else {
                        SearchResultUiState.Success(predictions)
                    }
                }
            }
        }.catch { SearchResultUiState.LoadFailed }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = SearchResultUiState.Loading,
        )

    val recentSearchQueriesUiState: StateFlow<RecentSearchQueriesUiState> =
        getRecentSearchQueryUseCase().map { recentQueries ->
            RecentSearchQueriesUiState.Success(recentQueries)
        }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = RecentSearchQueriesUiState.Loading,
        )

    fun handleEvent(event: StationMapEvent) {
        when (event) {
            is StationMapEvent.GetStationByCurrentLocation -> getStationByCurrentLocation()
            is StationMapEvent.ClearRecentSearches -> clearRecentSearches()
            is StationMapEvent.InsertRecentSearch -> insertRecentSearch(event.searchQuery)
            is StationMapEvent.GetStationByPlace -> getStationByPlace(event.placeId)
            is StationMapEvent.ResetMapCenter -> resetMapCenter()
            is StationMapEvent.UpdateSearchQuery -> onSearchQueryChanged(event.query)
            is StationMapEvent.ShowListStations -> showListStation(event.show)
        }
    }

    fun handleEventFilter(event: FiltersEvent) {
        when (event) {
            is FiltersEvent.UpdateBrandFilter -> updateFilterBrand(event.selected)
        }
    }

    private fun onSearchQueryChanged(query: String) {
        savedStateHandle[SEARCH_QUERY] = query
    }

    private fun getStationByCurrentLocation() {
        viewModelScope.launch {
            _state.update { it.copy(loading = true) }
            userLocation.getCurrentLocation()?.let { location ->
                getStationByLocation(location)
            }
        }
    }

    private fun clearRecentSearches() = viewModelScope.launch {
        clearRecentSearchQueriesUseCase()
    }

    private fun insertRecentSearch(searchQuery: SearchPlace) = viewModelScope.launch {
        insertRecentSearchQueryUseCase(placeId = searchQuery.id, name = searchQuery.name)
    }

    private fun getStationByPlace(placeId: String) =
        viewModelScope.launch {
            _state.update { it.copy(loading = true) }
            getLocationPlaceUseCase(placeId)
                .catch { _state.update { it.copy(loading = false) } }
                .collect { location ->
                    getStationByLocation(location)
                }
        }

    private fun resetMapCenter() = _state.update { it.copy(mapBounds = null) }

    private fun getStationByLocation(location: Location) {
        viewModelScope.launch {
            combine(
                fuelStationByLocation(
                    userLocation = location,
                    maxStations = 10
                ),
                getUserDataUseCase()
            ) { fuelStations, userData ->
                Pair(fuelStations, userData)
            }.catch { error ->
                _state.update { it.copy(error = error, loading = false) }
            }.collect { (fuelStations, userData) ->

                val allLocations = fuelStations.map { it.location.toLatLng() } + location.toLatLng()
                val boundsBuilder = LatLngBounds.Builder()
                allLocations.forEach { boundsBuilder.include(it) }
                val bounds = boundsBuilder.build()

                _state.update {
                    it.copy(
                        fuelStations = fuelStations,
                        selectedType = userData.fuelSelection,
                        loading = false,
                        mapBounds = bounds
                    )
                }
            }
        }
    }

    private fun getFilters() = viewModelScope.launch {
        _filterState.update {
            it.copy(
                filterBrand = listOf(
                    FuelStationBrandsType.CEPSA.value,
                    FuelStationBrandsType.REPSOL.value
                ),
                filterStationsNearby = 10,
                filterSchedule = OpeningHours.OPEN_24_H
            )
        }
    }

    private fun updateFilterBrand(stationsSelected: List<String>) {
        _filterState.update {
            it.copy(filterBrand = stationsSelected)
        }
    }

    private fun showListStation(show: Boolean) = _state.update { it.copy(showListStations = show) }
}
