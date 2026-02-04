---
title: State Collection in Compose
impact: MEDIUM
impactDescription: Ensures lifecycle-aware state collection and prevents memory leaks
tags: compose, lifecycle, state-collection, collectAsStateWithLifecycle, androidx
---

## State Collection in Compose

**Impact: MEDIUM (Ensures lifecycle-aware state collection and prevents memory leaks)**

Composables in GasGuru must collect ViewModel state using `collectAsStateWithLifecycle()` which respects the Android lifecycle. Never use non-null assertions (`!!`) when accessing state data.

**Incorrect (lifecycle-unaware collection, unsafe null handling):**

```kotlin
@Composable
fun StationSearchScreen(
    viewModel: StationSearchViewModel = hiltViewModel(),
) {
    // WRONG: collectAsState() doesn't respect lifecycle
    val state by viewModel.state.collectAsState()

    // WRONG: Using !! (non-null assertion)
    when (state) {
        is StationSearchUiState.Success -> {
            val stations = (state as StationSearchUiState.Success).stations!!
            StationList(stations = stations)
        }
    }
}
```

**Correct (lifecycle-aware collection, safe unwrapping):**

```kotlin
@Composable
fun StationSearchScreen(
    viewModel: StationSearchViewModel = hiltViewModel(),
) {
    // Lifecycle-aware collection
    val state by viewModel.state.collectAsStateWithLifecycle()

    when (state) {
        is StationSearchUiState.Loading -> {
            LoadingView()
        }
        is StationSearchUiState.Success -> {
            // Safe cast and access - no !!
            val data = (state as StationSearchUiState.Success)
            StationList(
                stations = data.stations,
                onStationClick = { stationId ->
                    // Handle click
                },
            )
        }
        is StationSearchUiState.Error -> {
            ErrorView()
        }
    }
}
```

**Real Example with Multiple StateFlows:**

```kotlin
@Composable
fun DetailStationScreen(
    viewModel: DetailStationViewModel = hiltViewModel(),
    onNavigateBack: () -> Unit,
) {
    val fuelStationState by viewModel.fuelStation.collectAsStateWithLifecycle()
    val staticMapUrl by viewModel.staticMapUrl.collectAsStateWithLifecycle()
    val lastUpdate by viewModel.lastUpdate.collectAsStateWithLifecycle()

    when (fuelStationState) {
        is DetailStationUiState.Loading -> {
            LoadingIndicator()
        }
        is DetailStationUiState.Success -> {
            val data = (fuelStationState as DetailStationUiState.Success)
            DetailStationContent(
                station = data.stationModel,
                address = data.address,
                mapUrl = staticMapUrl,
                lastUpdate = lastUpdate,
                onFavoriteClick = { isFavorite ->
                    viewModel.onEvent(
                        DetailStationEvent.ToggleFavorite(isFavorite = isFavorite)
                    )
                },
                onPriceAlertClick = { isEnabled ->
                    viewModel.onEvent(
                        DetailStationEvent.TogglePriceAlert(isEnabled = isEnabled)
                    )
                },
            )
        }
        is DetailStationUiState.Error -> {
            ErrorView(
                onRetry = { /* Retry logic */ },
                onBack = onNavigateBack,
            )
        }
    }
}
```

**Collecting Events with LaunchedEffect:**

For one-time events (navigation, show snackbar), use `LaunchedEffect`:

```kotlin
@Composable
fun StationSearchScreen(
    viewModel: StationSearchViewModel = hiltViewModel(),
    onNavigateToDetail: (Int) -> Unit,
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    // Collect one-time events
    LaunchedEffect(key1 = Unit) {
        viewModel.events.collect { event ->
            when (event) {
                is StationSearchEvent.NavigateToDetail -> {
                    onNavigateToDetail(event.stationId)
                }
                is StationSearchEvent.ShowMessage -> {
                    // Show snackbar
                }
            }
        }
    }

    when (state) {
        is StationSearchUiState.Loading -> LoadingView()
        is StationSearchUiState.Success -> {
            val data = (state as StationSearchUiState.Success)
            StationList(stations = data.stations)
        }
        is StationSearchUiState.Error -> ErrorView()
    }
}
```

**Key Points:**
- Always use `collectAsStateWithLifecycle()` in Composables
- Import from `androidx.lifecycle.compose.collectAsStateWithLifecycle`
- Never use `!!` for null assertions
- Use safe casts: `val data = (state as StationSearchUiState.Success)`
- Access properties directly from safe cast: `data.stations`
- Handle all sealed interface branches in `when` expression
- Use `LaunchedEffect` for one-time event collection
- Multiple StateFlows can be collected in the same Composable
- ViewModel is injected with `hiltViewModel()` from `androidx.hilt.navigation.compose`

**Common Mistakes to Avoid:**

```kotlin
// ❌ DON'T: collectAsState() without lifecycle
val state by viewModel.state.collectAsState()

// ❌ DON'T: Non-null assertion
val stations = state.stations!!

// ❌ DON'T: Nullable access without safe handling
val station = state.station?.let { it } ?: Station()

// ✅ DO: collectAsStateWithLifecycle
val state by viewModel.state.collectAsStateWithLifecycle()

// ✅ DO: Safe cast and access
when (state) {
    is Success -> {
        val data = (state as Success)
        StationList(stations = data.stations)
    }
}
```

For complete Compose and lifecycle guidelines, see [CLAUDE.md](file://../../../CLAUDE.md#compose--estado).