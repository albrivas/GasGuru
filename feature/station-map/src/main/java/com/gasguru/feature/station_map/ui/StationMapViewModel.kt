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
import com.gasguru.core.model.data.UserData
import com.gasguru.core.ui.models.FuelStationUiModel
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
            is StationMapEvent.GetStationByPlace -> getStationByPlace(placeId = event.placeId)
            is StationMapEvent.ShowListStations -> showListStation(event.show)
            is StationMapEvent.UpdateBrandFilter -> updateFilterBrand(stationsSelected = event.selected)
            is StationMapEvent.UpdateNearbyFilter -> updateFilterNearby(numberSelected = event.number)
            is StationMapEvent.UpdateScheduleFilter -> updateFilterSchedule(scheduleSelected = event.schedule)
            is StationMapEvent.OnMapCentered -> markMapAsCentered()
            is StationMapEvent.OnUserLocationCentered -> markUserLocationCentered()
            is StationMapEvent.StartRoute -> startRoute(
                originId = event.originId,
                destinationId = event.destinationId,
                destinationName = event.destinationName,
            )
            is StationMapEvent.CancelRoute -> cancelRoute()
            is StationMapEvent.ChangeTab -> changeTab(selectedTab = event.selected)
        }
    }

    private suspend fun getLocationById(placeId: String?): Location {
        return if (placeId != null) {
            getLocationPlaceUseCase(placeId = placeId).first()
        } else {
            getCurrentLocationUseCase() ?: throw Exception("Error to access location")
        }
    }

    private suspend fun getRouteLocations(
        originId: String?,
        destinationId: String?
    ): Pair<Location, Location> = coroutineScope {
        val originDeferred = async { getLocationById(placeId = originId) }
        val destinationDeferred = async { getLocationById(placeId = destinationId) }
        val locations = awaitAll(originDeferred, destinationDeferred)
        locations[0] to locations[1]
    }

    private fun handleRouteError(error: Exception) {
        _state.update {
            it.copy(
                error = error,
                loading = false,
                routeDestinationName = null
            )
        }
    }

    private suspend fun processRouteStations(
        origin: LatLng,
        route: com.gasguru.core.model.data.Route,
        destinationLocation: Location,
        destinationName: String?
    ) {
        try {
            val routeFuelStations = getFuelStationsInRouteUseCase(
                origin = origin,
                routePoints = route.route
            )
            val bounds = calculateRouteBounds(
                origin = Location("").apply {
                    latitude = origin.latitude
                    longitude = origin.longitude
                },
                destination = destinationLocation
            )
            val userData = getUserDataUseCase().first()
            val tabState = _tabState.value
            val uiStations = routeFuelStations.map { it.toUiModel() }
            val sortedStations = sortStationsByTab(
                stations = uiStations,
                selectedTab = tabState.selectedTab,
                userData = userData
            )

            _state.update {
                it.copy(
                    mapStations = uiStations,
                    listStations = sortedStations,
                    route = route,
                    routeDestinationName = destinationName,
                    mapBounds = bounds,
                    shouldCenterMap = true,
                    loading = false,
                )
            }
        } catch (error: Exception) {
            handleRouteError(error = error)
        }
    }

    private fun startRoute(
        originId: String?,
        destinationId: String?,
        destinationName: String?
    ) = viewModelScope.launch {
        _state.update {
            it.copy(
                loading = true,
                listStations = emptyList(),
                routeDestinationName = destinationName
            )
        }

        try {
            val (originLocation, destinationLocation) = getRouteLocations(
                originId = originId,
                destinationId = destinationId
            )
            val origin = LatLng(originLocation.latitude, originLocation.longitude)
            val destination = LatLng(destinationLocation.latitude, destinationLocation.longitude)

            getRouteUseCase(origin = origin, destination = destination).collect { route ->
                route?.let { routeData ->
                    launch(defaultDispatcher) {
                        processRouteStations(
                            origin = origin,
                            route = routeData,
                            destinationLocation = destinationLocation,
                            destinationName = destinationName
                        )
                    }
                }
            }
        } catch (error: Exception) {
            handleRouteError(error = error)
        }
    }

    private fun markMapAsCentered() {
        _state.update { it.copy(shouldCenterMap = false) }
    }

    private fun markUserLocationCentered() {
        _state.update { it.copy(userLocationToCenter = null) }
    }

    private fun cancelRoute() {
        _state.update { it.copy(route = null, routeDestinationName = null) }
        getStationByCurrentLocation()
    }

    private fun getStationByCurrentLocation() {
        viewModelScope.launch {
            getCurrentLocationUseCase()?.let { location ->
                if (_state.value.route != null) {
                    centerMapOnLocation(location = location)
                } else {
                    _state.update { it.copy(loading = true, listStations = emptyList()) }
                    getStationByLocation(location = location)
                }
            }
        }
    }

    private fun centerMapOnLocation(location: Location) {
        _state.update {
            it.copy(userLocationToCenter = location.toLatLng())
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
                getUserDataUseCase()
            ) { filterState, userData ->
                Pair(filterState, userData)
            }.collectLatest { (filterState, userData) ->
                if (_state.value.route != null) return@collectLatest
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
                    _state.update {
                        it.copy(
                            mapStations = uiStations,
                            listStations = sortStationsByTab(uiStations, _tabState.value.selectedTab, userData),
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

    private fun changeTab(selectedTab: StationSortTab) {
        _tabState.update { it.copy(selectedTab = selectedTab) }

        val currentState = _state.value
        if (currentState.mapStations.isNotEmpty()) {
            viewModelScope.launch(defaultDispatcher) {
                val userData = getUserDataUseCase().first()
                val sortedStations = sortStationsByTab(currentState.mapStations, selectedTab, userData)
                _state.update { it.copy(listStations = sortedStations) }
            }
        }
    }

    private fun sortStationsByTab(
        stations: List<FuelStationUiModel>,
        selectedTab: StationSortTab,
        userData: UserData
    ) = when (selectedTab) {
        StationSortTab.PRICE -> stations.sortedBy { userData.fuelSelection.extractPrice(it.fuelStation) }
        StationSortTab.DISTANCE -> stations.sortedBy { it.fuelStation.distance }
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
