---
name: skill-create-viewmodel
description: Generate Android ViewModels following GasGuru architecture patterns. Use when creating or modifying ViewModels with sealed UiState, events, and Koin injection (NOT Hilt — GasGuru uses Koin). Triggers on tasks involving new features, state management, ViewModel creation, or modifying existing ViewModels.
metadata:
  author: Alberto Rivas
  version: "1.1.0"
  project: GasGuru
---

# GasGuru ViewModel Generator

Comprehensive guide for generating Android ViewModels following GasGuru's architecture patterns. Uses **Koin** for dependency injection (not Hilt). Contains 5 rules across 3 priority levels, ensuring proper state management, dependency injection, and architectural compliance.

## When to Apply

Reference these guidelines when:
- Creating new feature modules with ViewModels
- Modifying existing ViewModels
- Implementing state management for Compose screens
- Setting up event handling in ViewModels
- Integrating UseCases with Koin dependency injection
- Refactoring existing ViewModels to follow architecture

## Rule Categories by Priority

| Priority | Category | Impact | Prefix |
|----------|----------|--------|--------|
| 1 | Architecture Compliance | CRITICAL | `viewmodel-`, `koin-` |
| 2 | State & Event Patterns | HIGH | `uistate-`, `events-` |
| 3 | Lifecycle Management | MEDIUM | `state-collection-` |

## Quick Reference

### 1. Architecture Compliance (CRITICAL)

- `viewmodel-structure` - Plain `ViewModel()` base class, registered in Koin module with `viewModel { ... }`
- `koin-injection` - Inject UseCases via constructor, never access repositories directly

### 2. State & Event Patterns (HIGH)

- `uistate-sealed` - Use sealed interface with Loading/Success/Error states
- `events-pattern` - Define sealed Event class with handleEvent() function

### 3. Lifecycle Management (MEDIUM)

- `state-collection` - Use collectAsStateWithLifecycle(), avoid !! assertions

## Project Architecture

This skill follows GasGuru architecture patterns defined in [CLAUDE.md](file://../../../CLAUDE.md). The skill injects these rules dynamically when invoked:

!`grep -A 10 "^## Compose & Estado" CLAUDE.md`

!`grep -A 3 "^## Módulos y reglas" CLAUDE.md`

For complete guidelines including code standards, module dependencies, and theming rules, see [CLAUDE.md](file://../../../CLAUDE.md).

## How to Use with Arguments

Invoke with feature name or ViewModel name:

```
/viewmodel StationSearch
```

The skill will guide you to generate:
1. `XxxUiState.kt` - Sealed interface with Loading/Success/Error states
2. `XxxEvent.kt` - Sealed class for user actions
3. `XxxViewModel.kt` - ViewModel with @HiltViewModel annotation
4. Usage example in Composable with proper state collection

## File Structure

Generated files follow GasGuru's feature module structure:

```
feature/<feature-name>/
└── src/main/java/com/gasguru/feature/<feature_name>/ui/
    ├── XxxUiState.kt
    ├── XxxEvent.kt
    ├── XxxViewModel.kt
    └── XxxScreen.kt (usage example)
```

## Full Rule Details

For detailed explanations and code examples, read individual rule files:

```
rules/viewmodel-structure.md
rules/uistate-sealed.md
rules/events-pattern.md
rules/hilt-injection.md
rules/state-collection.md
```

Each rule file contains:
- Impact level and description
- Incorrect code example with explanation
- Correct code example with explanation
- References to actual GasGuru code

## Example Generation

When invoked with `/viewmodel StationSearch`, the skill generates:

**StationSearchUiState.kt:**
```kotlin
package com.gasguru.feature.station_search.ui

import com.gasguru.core.ui.models.FuelStationUiModel

sealed interface StationSearchUiState {
    data object Loading : StationSearchUiState
    data class Success(val stations: List<FuelStationUiModel>) : StationSearchUiState
    data object Error : StationSearchUiState
}
```

**StationSearchEvent.kt:**
```kotlin
package com.gasguru.feature.station_search.ui

sealed class StationSearchEvent {
    data class SearchByName(val query: String) : StationSearchEvent
    data object ClearSearch : StationSearchEvent
}
```

**StationSearchViewModel.kt:**
```kotlin
package com.gasguru.feature.station_search.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.gasguru.core.domain.fuelstation.SearchStationsUseCase
import com.gasguru.core.ui.toUiModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class StationSearchViewModel(
    private val searchStationsUseCase: SearchStationsUseCase,
) : ViewModel() {

    private val _state = MutableStateFlow<StationSearchUiState>(StationSearchUiState.Loading)
    val state: StateFlow<StationSearchUiState> = _state

    fun handleEvent(event: StationSearchEvent) {
        when (event) {
            is StationSearchEvent.SearchByName -> searchByName(query = event.query)
            is StationSearchEvent.ClearSearch -> clearSearch()
        }
    }

    private fun searchByName(query: String) = viewModelScope.launch {
        _state.update { StationSearchUiState.Loading }
        try {
            searchStationsUseCase(query = query).collect { stations ->
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
}
```

## Verification Checklist

After generating ViewModels, verify:

- [ ] ViewModel is a plain `class` extending `ViewModel()` — no `@HiltViewModel`
- [ ] ViewModel is registered in the Koin module with `viewModel { XxxViewModel(get()) }`
- [ ] UiState is sealed interface with Loading/Success/Error
- [ ] Events are sealed interface with `handleEvent()` function
- [ ] Only UseCases are injected via constructor, no repositories
- [ ] StateFlow uses `SharingStarted.WhileSubscribed(5_000)` when using `stateIn()`
- [ ] Code follows CLAUDE.md standards (named arguments, trailing commas, no hardcoded strings)