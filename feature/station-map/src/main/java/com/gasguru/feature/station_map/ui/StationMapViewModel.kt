package com.gasguru.feature.station_map.ui

import android.location.Location
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.gasguru.core.common.toLatLng
import com.gasguru.core.domain.ClearRecentSearchQueriesUseCase
import com.gasguru.core.domain.FuelStationByLocationUseCase
import com.gasguru.core.domain.GetFiltersUseCase
import com.gasguru.core.domain.GetLocationPlaceUseCase
import com.gasguru.core.domain.GetPlacesUseCase
import com.gasguru.core.domain.GetRecentSearchQueryUseCase
import com.gasguru.core.domain.GetUserDataUseCase
import com.gasguru.core.domain.InsertRecentSearchQueryUseCase
import com.gasguru.core.domain.SaveFilterUseCase
import com.gasguru.core.domain.location.GetCurrentLocationUseCase
import com.gasguru.core.domain.route.GetRouteUseCase
import com.gasguru.core.model.data.Filter
import com.gasguru.core.model.data.FilterType
import com.gasguru.core.model.data.FuelStation
import com.gasguru.core.model.data.Route
import com.gasguru.core.model.data.SearchPlace
import com.google.android.gms.maps.model.LatLngBounds
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collectLatest
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
    private val getUserDataUseCase: GetUserDataUseCase,
    private val savedStateHandle: SavedStateHandle,
    private val getPlacesUseCase: GetPlacesUseCase,
    private val getLocationPlaceUseCase: GetLocationPlaceUseCase,
    private val clearRecentSearchQueriesUseCase: ClearRecentSearchQueriesUseCase,
    private val insertRecentSearchQueryUseCase: InsertRecentSearchQueryUseCase,
    getRecentSearchQueryUseCase: GetRecentSearchQueryUseCase,
    private val getCurrentLocationUseCase: GetCurrentLocationUseCase,
    getFiltersUseCase: GetFiltersUseCase,
    private val saveFilterUseCase: SaveFilterUseCase,
    private val getRouteUseCase: GetRouteUseCase,
) : ViewModel() {

    val searchQuery = savedStateHandle.getStateFlow(key = SEARCH_QUERY, initialValue = "")

    private val _state = MutableStateFlow(StationMapUiState())
    val state: StateFlow<StationMapUiState> = _state

    init {
        getStationByCurrentLocation()
    }

    val polyline: StateFlow<Route?> = getRouteUseCase(
        origin = Location("").apply {
            latitude = 40.483785
            longitude = -3.700888
        },
        destination = Location("").apply {
            latitude = 40.480084
            longitude = -3.704560
        }
    ).stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = null
    )

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
            is StationMapEvent.UpdateBrandFilter -> updateFilterBrand(event.selected)
            is StationMapEvent.UpdateNearbyFilter -> updateFilterNearby(event.number)
            is StationMapEvent.UpdateScheduleFilter -> updateFilterSchedule(event.schedule)
        }
    }

    private fun onSearchQueryChanged(query: String) {
        savedStateHandle[SEARCH_QUERY] = query
    }

    private fun getStationByCurrentLocation() {
        viewModelScope.launch {
            _state.update { it.copy(loading = true) }
            getCurrentLocationUseCase()?.let { location ->
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
                filters,
                getUserDataUseCase()
            ) { filterState, userData ->
                Pair(filterState, userData)
            }.collectLatest { (filterState, userData) ->
                fuelStationByLocation(
                    userLocation = location,
                    maxStations = filterState.filterStationsNearby,
                    brands = filterState.filterBrand,
                    schedule = filterState.filterSchedule.toDomainModel()
                ).catch { error ->
                    _state.update { it.copy(error = error, loading = false) }
                }.collect { fuelStations ->
                    val bounds = calculateBounds(fuelStations = fuelStations, location = location)
                    _state.update {
                        it.copy(
                            fuelStations = fuelStations,
                            loading = false,
                            selectedType = userData.fuelSelection,
                            mapBounds = bounds
                        )
                    }
                }
            }
        }
    }

    private fun calculateBounds(fuelStations: List<FuelStation>, location: Location): LatLngBounds {
        val allLocations =
            fuelStations.map { it.location.toLatLng() } + location.toLatLng()
        val boundsBuilder = LatLngBounds.Builder()
        allLocations.forEach { boundsBuilder.include(it) }
        return boundsBuilder.build()
    }

    val filters: StateFlow<FilterUiState> = getFiltersUseCase()
        .map { filters ->
            val filtersByType = filters.associateBy { it.type }

            FilterUiState(
                filterBrand = filtersByType.getBrandFilter(),
                filterStationsNearby = filtersByType.getNearbyFilter(),
                filterSchedule = filtersByType.getScheduleFilter()
            )
        }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = FilterUiState(),
        )

    private fun updateFilterBrand(stationsSelected: List<String>) = viewModelScope.launch {
        saveFilterUseCase(filterType = FilterType.BRAND, selection = stationsSelected)
    }

    private fun updateFilterNearby(numberSelected: String) = viewModelScope.launch {
        saveFilterUseCase(filterType = FilterType.NEARBY, selection = listOf(numberSelected))
    }

    private fun updateFilterSchedule(scheduleSelected: FilterUiState.OpeningHours) =
        viewModelScope.launch {
            saveFilterUseCase(
                filterType = FilterType.SCHEDULE,
                selection = listOf(scheduleSelected.name)
            )
        }

    private fun showListStation(show: Boolean) = _state.update { it.copy(showListStations = show) }

    private fun Map<FilterType, Filter>.getBrandFilter() =
        this[FilterType.BRAND]?.selection ?: emptyList()

    private fun Map<FilterType, Filter>.getNearbyFilter() =
        this[FilterType.NEARBY]?.selection?.firstOrNull()?.toIntOrNull() ?: 10

    private fun Map<FilterType, Filter>.getScheduleFilter() =
        this[FilterType.SCHEDULE]?.selection?.firstOrNull()
            ?.let { FilterUiState.OpeningHours.valueOf(it) } ?: FilterUiState.OpeningHours.NONE

    private fun FilterUiState.OpeningHours.toDomainModel(): com.gasguru.core.model.data.OpeningHours =
        when (this) {
            FilterUiState.OpeningHours.NONE -> com.gasguru.core.model.data.OpeningHours.NONE
            FilterUiState.OpeningHours.OPEN_NOW -> com.gasguru.core.model.data.OpeningHours.OPEN_NOW
            FilterUiState.OpeningHours.OPEN_24_H -> com.gasguru.core.model.data.OpeningHours.OPEN_24H
        }
}
