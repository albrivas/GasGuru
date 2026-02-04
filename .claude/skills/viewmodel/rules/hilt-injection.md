---
title: Hilt Dependency Injection with UseCases
impact: CRITICAL
impactDescription: Ensures proper layered architecture and testability
tags: hilt, dependency-injection, usecases, architecture, clean-architecture
---

## Hilt Dependency Injection with UseCases

**Impact: CRITICAL (Ensures proper layered architecture and testability)**

ViewModels in GasGuru must only inject UseCases from the domain layer, never repositories or data sources directly. This maintains proper separation of concerns and follows Clean Architecture principles.

**Incorrect (direct repository access, violates architecture layers):**

```kotlin
@HiltViewModel
class StationSearchViewModel @Inject constructor(
    private val stationRepository: StationRepository, // WRONG: Direct repository access
    private val database: StationDatabase,            // WRONG: Direct data source access
    private val apiService: FuelStationApi,           // WRONG: Direct network access
) : ViewModel() {

    fun search(query: String) = viewModelScope.launch {
        // ViewModel contains business logic that should be in domain layer
        val stations = stationRepository.searchStations(query)
        val filtered = stations.filter { it.price < 100 }
        _state.update { StationSearchUiState.Success(filtered) }
    }
}
```

**Correct (UseCase injection, clean architecture):**

```kotlin
@HiltViewModel
class StationSearchViewModel @Inject constructor(
    private val searchStationsUseCase: SearchStationsUseCase,
    private val filterStationsByPriceUseCase: FilterStationsByPriceUseCase,
    private val getUserPreferencesUseCase: GetUserPreferencesUseCase,
) : ViewModel() {

    fun handleEvent(event: StationSearchEvent) {
        when (event) {
            is StationSearchEvent.SearchByName -> searchByName(query = event.query)
        }
    }

    private fun searchByName(query: String) = viewModelScope.launch {
        _state.update { StationSearchUiState.Loading }
        try {
            // Business logic is in UseCases, ViewModel just orchestrates
            searchStationsUseCase(query = query).collect { stations ->
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

**Real Example from GasGuru (DetailStationViewModel):**

```kotlin
@HiltViewModel
class DetailStationViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    getFuelStationByIdUseCase: GetFuelStationByIdUseCase,
    getLastKnownLocationUseCase: GetLastKnownLocationUseCase,
    private val userDataUseCase: GetUserDataUseCase,
    private val saveFavoriteStationUseCase: SaveFavoriteStationUseCase,
    private val removeFavoriteStationUseCase: RemoveFavoriteStationUseCase,
    private val getAddressFromLocationUseCase: GetAddressFromLocationUseCase,
    private val getStaticMapUrlUseCase: GetStaticMapUrlUseCase,
    private val addPriceAlertUseCase: AddPriceAlertUseCase,
    private val removePriceAlertUseCase: RemovePriceAlertUseCase,
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

    private fun onFavoriteClick(isFavorite: Boolean) = viewModelScope.launch {
        when (isFavorite) {
            true -> saveFavoriteStationUseCase(stationId = id)
            false -> removeFavoriteStationUseCase(stationId = id)
        }
    }
}
```

**Named Arguments for Dispatchers:**

When injecting coroutine dispatchers, use named qualifiers:

```kotlin
@HiltViewModel
class StationMapViewModel @Inject constructor(
    private val fuelStationByLocation: FuelStationByLocationUseCase,
    private val getUserDataUseCase: GetUserDataUseCase,
    @DefaultDispatcher private val defaultDispatcher: CoroutineDispatcher,
    @IoDispatcher private val ioDispatcher: CoroutineDispatcher,
) : ViewModel() {
    // Implementation
}
```

**Key Points:**
- Only inject UseCases, never repositories or data sources
- Use `private val` for all injected dependencies
- Use named arguments in constructor: `@DefaultDispatcher private val defaultDispatcher`
- UseCases should be from `core:domain` module
- ViewModel orchestrates UseCases, doesn't contain business logic
- Use `SavedStateHandle` when you need to retrieve navigation arguments
- Extract navigation args early: `private val id: Int = checkNotNull(savedStateHandle["key"])`
- UseCases return `Flow` for reactive data or suspend functions for one-time operations
- Use `.toUiModel()` extension functions to convert domain models to UI models

Reference: [DetailStationViewModel.kt](file://../../../../feature/detail-station/src/main/java/com/gasguru/feature/detail_station/ui/DetailStationViewModel.kt)