package com.gasguru.feature.station_map.ui

import android.location.Location
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.gasguru.core.common.DefaultDispatcher
import com.gasguru.core.common.toLatLng
import com.gasguru.core.domain.filters.GetFiltersUseCase
import com.gasguru.core.domain.filters.SaveFilterUseCase
import com.gasguru.core.domain.fuelstation.FuelStationByLocationUseCase
import com.gasguru.core.domain.fuelstation.GetFuelStationsInRouteUseCase
import com.gasguru.core.domain.location.GetCurrentLocationUseCase
import com.gasguru.core.domain.places.GetLocationPlaceUseCase
import com.gasguru.core.domain.route.GetRouteUseCase
import com.gasguru.core.domain.user.GetUserDataUseCase
import com.gasguru.core.model.data.Filter
import com.gasguru.core.model.data.FilterType
import com.gasguru.core.model.data.LatLng
import com.gasguru.core.ui.toUiModel
import com.google.android.gms.maps.model.LatLngBounds
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class StationMapViewModel @Inject constructor(
    private val fuelStationByLocation: FuelStationByLocationUseCase,
    private val getUserDataUseCase: GetUserDataUseCase,
    private val getLocationPlaceUseCase: GetLocationPlaceUseCase,
    private val getCurrentLocationUseCase: GetCurrentLocationUseCase,
    getFiltersUseCase: GetFiltersUseCase,
    private val saveFilterUseCase: SaveFilterUseCase,
    private val getRouteUseCase: GetRouteUseCase,
    private val getFuelStationsInRouteUseCase: GetFuelStationsInRouteUseCase,
    @DefaultDispatcher private val defaultDispatcher: CoroutineDispatcher,
) : ViewModel() {

    private val _state = MutableStateFlow(StationMapUiState())
    val state: StateFlow<StationMapUiState> = _state

    private val _tabState = MutableStateFlow(SelectedTabUiState())
    val tabState: StateFlow<SelectedTabUiState> = _tabState

    init {
        getStationByCurrentLocation()
    }

    fun handleEvent(event: StationMapEvent) {
        when (event) {
            is StationMapEvent.GetStationByCurrentLocation -> getStationByCurrentLocation()
            is StationMapEvent.GetStationByPlace -> getStationByPlace(event.placeId)
            is StationMapEvent.ShowListStations -> showListStation(event.show)
            is StationMapEvent.UpdateBrandFilter -> updateFilterBrand(event.selected)
            is StationMapEvent.UpdateNearbyFilter -> updateFilterNearby(event.number)
            is StationMapEvent.UpdateScheduleFilter -> updateFilterSchedule(event.schedule)
            is StationMapEvent.OnMapCentered -> markMapAsCentered()
            is StationMapEvent.StartRoute -> startRoute(event.originId, event.destinationId)
            is StationMapEvent.ChangeTab -> changeTab(event.selected)
        }
    }

    private fun startRoute(originId: String?, destinationId: String?) = viewModelScope.launch {
        _state.update { it.copy(loading = true, fuelStations = emptyList()) }

        try {
            val (originLocation, destinationLocation) = coroutineScope {
                val originDeferred = async {
                    if (originId != null) {
                        getLocationPlaceUseCase(placeId = originId).first()
                    } else {
                        getCurrentLocationUseCase() ?: throw Exception("Error to access location")
                    }
                }
                val destinationDeferred = async {
                    if (destinationId != null) {
                        getLocationPlaceUseCase(placeId = destinationId).first()
                    } else {
                        getCurrentLocationUseCase() ?: throw Exception("Error to access location")
                    }
                }

                awaitAll(originDeferred, destinationDeferred)
            }

            val origin = LatLng(
                originLocation.latitude,
                originLocation.longitude
            )
            getRouteUseCase(
                origin = origin,
                destination = LatLng(
                    destinationLocation.latitude,
                    destinationLocation.longitude
                )
            ).collect { route ->

                route?.let { routeData ->
                    launch(defaultDispatcher) {
                        try {
                            val routeFuelStations =
                                getFuelStationsInRouteUseCase(
                                    origin = origin,
                                    routePoints = routeData.route
                                )
                            val bounds = calculateRouteBounds(
                                origin = originLocation,
                                destination = destinationLocation
                            )
                            _state.update {
                                it.copy(
                                    fuelStations = routeFuelStations.map { station -> station.toUiModel() },
                                    route = route,
                                    mapBounds = bounds,
                                    shouldCenterMap = true,
                                    loading = false
                                )
                            }
                        } catch (error: Exception) {
                            _state.update { it.copy(error = error, loading = false) }
                        }
                    }
                }
            }
        } catch (error: Exception) {
            _state.update { it.copy(error = error, loading = false) }
        }
    }

    private fun markMapAsCentered() {
        _state.update { it.copy(shouldCenterMap = false) }
    }

    private fun getStationByCurrentLocation() {
        viewModelScope.launch {
            _state.update { it.copy(loading = true, route = null, fuelStations = emptyList()) }
            getCurrentLocationUseCase()?.let { location ->
                getStationByLocation(location)
            }
        }
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

    private fun getStationByLocation(location: Location) {
        viewModelScope.launch {
            combine(
                filters,
                getUserDataUseCase(),
                _tabState
            ) { filterState, userData, tabState ->
                Triple(filterState, userData, tabState)
            }.collectLatest { (filterState, userData, tabState) ->
                fuelStationByLocation(
                    userLocation = location,
                    maxStations = filterState.filterStationsNearby,
                    brands = filterState.filterBrand,
                    schedule = filterState.filterSchedule.toDomainModel()
                ).catch { error ->
                    _state.update { it.copy(error = error, loading = false) }
                }.collect { fuelStations ->
                    val bounds = calculateBounds(
                        fuelStations = fuelStations.map { it.location },
                        location = location
                    )
                    val uiStations = fuelStations.map { station -> station.toUiModel() }
                    val sortedStations = when (tabState.selectedTab) {
                        0 -> uiStations.sortedBy { userData.fuelSelection.extractPrice(it.fuelStation) }
                        1 -> uiStations.sortedBy { it.formattedDistance }
                        else -> uiStations
                    }
                    _state.update {
                        it.copy(
                            fuelStations = sortedStations,
                            loading = false,
                            selectedType = userData.fuelSelection,
                            mapBounds = bounds,
                            shouldCenterMap = true,
                        )
                    }
                }
            }
        }
    }

    private fun calculateBounds(fuelStations: List<Location>, location: Location): LatLngBounds {
        val allLocations =
            fuelStations.map { it.toLatLng() } + location.toLatLng()
        val boundsBuilder = LatLngBounds.Builder()
        allLocations.forEach { boundsBuilder.include(it) }
        return boundsBuilder.build()
    }

    private fun calculateRouteBounds(
        origin: Location,
        destination: Location
    ): LatLngBounds {
        val allLocations = listOf(origin.toLatLng(), destination.toLatLng())
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

    private fun changeTab(selectedTab: Int) {
        _tabState.update { it.copy(selectedTab = selectedTab) }
    }

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
