---
title: Events Pattern with Sealed Classes
impact: HIGH
impactDescription: Centralizes user actions and ensures type-safe event handling
tags: events, sealed-class, event-handling, user-actions, architecture
---

## Events Pattern with Sealed Classes

**Impact: HIGH (Centralizes user actions and ensures type-safe event handling)**

ViewModels in GasGuru must define user actions as sealed classes/interfaces and handle them through a single `handleEvent()` or `onEvent()` function. This pattern centralizes event handling and makes all possible user actions explicit.

**Incorrect (multiple public functions exposed to UI, scattered event handling):**

```kotlin
@HiltViewModel
class StationSearchViewModel @Inject constructor(
    private val searchStationsUseCase: SearchStationsUseCase,
) : ViewModel() {

    // Multiple public functions - harder to track all possible actions
    fun searchByName(query: String) {
        viewModelScope.launch {
            // Search logic
        }
    }

    fun clearSearch() {
        // Clear logic
    }

    fun filterByBrand(brand: String) {
        // Filter logic
    }

    // UI has to know about 3+ different functions
}
```

**Correct (sealed Event class with centralized handler):**

```kotlin
// Event file (StationSearchEvent.kt)
package com.gasguru.feature.station_search.ui

sealed class StationSearchEvent {
    data class SearchByName(val query: String) : StationSearchEvent()
    data object ClearSearch : StationSearchEvent()
    data class FilterByBrand(val brand: String) : StationSearchEvent()
}

// ViewModel file (StationSearchViewModel.kt)
@HiltViewModel
class StationSearchViewModel @Inject constructor(
    private val searchStationsUseCase: SearchStationsUseCase,
) : ViewModel() {

    private val _state = MutableStateFlow<StationSearchUiState>(StationSearchUiState.Loading)
    val state: StateFlow<StationSearchUiState> = _state

    fun handleEvent(event: StationSearchEvent) {
        when (event) {
            is StationSearchEvent.SearchByName -> searchByName(query = event.query)
            is StationSearchEvent.ClearSearch -> clearSearch()
            is StationSearchEvent.FilterByBrand -> filterByBrand(brand = event.brand)
        }
    }

    private fun searchByName(query: String) = viewModelScope.launch {
        _state.update { StationSearchUiState.Loading }
        try {
            searchStationsUseCase(query = query).collect { stations →
                _state.update {
                    StationSearchUiState.Success(stations = stations.map { it.toUiModel() })
                }
            }
        } catch (e: Exception) {
            _state.update { StationSearchUiState.Error }
        }
    }

    private fun clearSearch() {
        _state.update { StationSearchUiState.Success(stations = emptyList()) }
    }

    private fun filterByBrand(brand: String) {
        // Filter implementation
    }
}
```

**Real Example from GasGuru:**

```kotlin
// DetailStationEvent.kt
package com.gasguru.feature.detail_station.ui

sealed interface DetailStationEvent {
    data class ToggleFavorite(val isFavorite: Boolean) : DetailStationEvent
    data class TogglePriceAlert(val isEnabled: Boolean) : DetailStationEvent
}

// DetailStationViewModel.kt
@HiltViewModel
class DetailStationViewModel @Inject constructor(
    private val saveFavoriteStationUseCase: SaveFavoriteStationUseCase,
    private val removeFavoriteStationUseCase: RemoveFavoriteStationUseCase,
    private val addPriceAlertUseCase: AddPriceAlertUseCase,
    private val removePriceAlertUseCase: RemovePriceAlertUseCase,
) : ViewModel() {

    fun onEvent(event: DetailStationEvent) {
        when (event) {
            is DetailStationEvent.ToggleFavorite -> {
                onFavoriteClick(event.isFavorite)
            }
            is DetailStationEvent.TogglePriceAlert -> {
                onPriceAlertClick(event.isEnabled)
            }
        }
    }

    private fun onFavoriteClick(isFavorite: Boolean) = viewModelScope.launch {
        when (isFavorite) {
            true -> saveFavoriteStationUseCase(stationId = id)
            false -> removeFavoriteStationUseCase(stationId = id)
        }
    }

    private fun onPriceAlertClick(isEnabled: Boolean) = viewModelScope.launch {
        when (isEnabled) {
            true -> addPriceAlertUseCase(stationId = id, lastNotifiedPrice = price)
            false -> removePriceAlertUseCase(stationId = id)
        }
    }
}
```

**Key Points:**
- Use `sealed class` or `sealed interface` for events
- Use `data class` for events with parameters
- Use `data object` for events without parameters
- Single `handleEvent()` or `onEvent()` function as entry point
- Use `when` expression for exhaustive handling
- Private functions for actual implementation
- Named arguments when calling private functions: `searchByName(query = event.query)`

**Usage in Composable:**

```kotlin
@Composable
fun StationSearchScreen(
    viewModel: StationSearchViewModel = hiltViewModel(),
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    LaunchedEffect(Unit) {
        viewModel.handleEvent(StationSearchEvent.SearchByName(query = "Shell"))
    }

    when (state) {
        is StationSearchUiState.Loading -> LoadingView()
        is StationSearchUiState.Success -> {
            val data = (state as StationSearchUiState.Success).stations
            StationList(
                stations = data,
                onStationClick = { stationId ->
                    // Navigate with ID only (see navigation-ids.md)
                },
            )
        }
        is StationSearchUiState.Error -> ErrorView()
    }
}
```

Reference: [DetailStationEvent.kt](file://../../../../feature/detail-station/src/main/java/com/gasguru/feature/detail_station/ui/DetailStationEvent.kt)