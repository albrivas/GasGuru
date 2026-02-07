---
title: Sealed UiState Pattern
impact: HIGH
impactDescription: Ensures type-safe state management with clear loading/success/error states
tags: state, sealed-class, uistate, state-management, type-safety
---

## Sealed UiState Pattern

**Impact: HIGH (Ensures type-safe state management with clear loading/success/error states)**

GasGuru ViewModels must expose state using sealed interfaces with Loading, Success, and Error states. This pattern provides compile-time safety for state handling and makes all possible states explicit in the UI layer.

**Incorrect (mutable state, unclear state pattern, missing error handling):**

```kotlin
// Multiple unrelated StateFlows without clear pattern
class StationSearchViewModel @Inject constructor() : ViewModel() {
    private val _stations = MutableStateFlow<List<Station>>(emptyList())
    val stations: StateFlow<List<Station>> = _stations

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error

    // UI has to observe 3 different flows
}
```

**Correct (sealed interface with unified state):**

```kotlin
// UiState file (StationSearchUiState.kt)
package com.gasguru.feature.station_search.ui

import com.gasguru.core.ui.models.FuelStationUiModel

sealed interface StationSearchUiState {
    data object Loading : StationSearchUiState
    data class Success(val stations: List<FuelStationUiModel>) : StationSearchUiState
    data object Error : StationSearchUiState
}

// ViewModel file (StationSearchViewModel.kt)
@HiltViewModel
class StationSearchViewModel @Inject constructor(
    private val searchStationsUseCase: SearchStationsUseCase,
) : ViewModel() {

    private val _state = MutableStateFlow<StationSearchUiState>(StationSearchUiState.Loading)
    val state: StateFlow<StationSearchUiState> = _state

    init {
        loadStations()
    }

    private fun loadStations() = viewModelScope.launch {
        _state.update { StationSearchUiState.Loading }
        try {
            searchStationsUseCase().collect { stations ->
                _state.update {
                    StationSearchUiState.Success(stations = stations.map { it.toUiModel() })
                }
            }
        } catch (e: Exception) {
            _state.update { StationSearchUiState.Error }
        }
    }
}
```

**Advanced Pattern with stateIn() for reactive flows:**

```kotlin
@HiltViewModel
class DetailStationViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    getFuelStationByIdUseCase: GetFuelStationByIdUseCase,
    getLastKnownLocationUseCase: GetLastKnownLocationUseCase,
) : ViewModel() {

    private val id: Int = checkNotNull(savedStateHandle["idServiceStation"])

    @OptIn(ExperimentalCoroutinesApi::class)
    val fuelStation: StateFlow<DetailStationUiState> = getLastKnownLocationUseCase()
        .flatMapLatest { location ->
            location?.let { safeLocation ->
                getFuelStationByIdUseCase(id = id, userLocation = safeLocation)
                    .map { station ->
                        DetailStationUiState.Success(
                            stationModel = station.toUiModel(),
                            address = null,
                        )
                    }
                    .catch { emit(DetailStationUiState.Error) }
            } ?: flowOf(DetailStationUiState.Error)
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = DetailStationUiState.Loading,
        )
}
```

**Key Points:**
- Use `sealed interface` for UiState (not sealed class)
- Always include Loading, Success, and Error states minimum
- Use `data object` for states without data (Loading, Error)
- Use `data class` for states with data (Success)
- Private `MutableStateFlow`, public `StateFlow`
- When using `stateIn()`, use `SharingStarted.WhileSubscribed(5_000)` for lifecycle awareness
- Update state with `_state.update { newState }` for thread safety

Reference: [DetailStationUiState.kt](file://../../../../feature/detail-station/src/main/java/com/gasguru/feature/detail_station/ui/DetailStationUiState.kt)