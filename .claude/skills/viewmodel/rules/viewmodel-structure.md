---
title: ViewModel Structure with Hilt
impact: CRITICAL
impactDescription: Essential for dependency injection and Android lifecycle compliance
tags: architecture, hilt, viewmodel, structure, dependency-injection
---

## ViewModel Structure with Hilt

**Impact: CRITICAL (Essential for dependency injection and Android lifecycle compliance)**

All ViewModels in GasGuru must use `@HiltViewModel` annotation for Dagger-Hilt dependency injection and extend `ViewModel()` base class for proper Android lifecycle management. This ensures consistent dependency injection across the app and proper cleanup of resources.

**Incorrect (missing @HiltViewModel, wrong dependency management):**

```kotlin
// Missing @HiltViewModel annotation
// Manual dependency instantiation
// Not extending ViewModel
class StationSearchViewModel(
    private val repository: StationRepository
) {
    private val searchUseCase = SearchStationsUseCase(repository)

    fun search(query: String) {
        // Implementation
    }
}
```

**Correct (proper Hilt integration with ViewModel base class):**

```kotlin
package com.gasguru.feature.station_search.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.gasguru.core.domain.fuelstation.SearchStationsUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject

@HiltViewModel
class StationSearchViewModel @Inject constructor(
    private val searchStationsUseCase: SearchStationsUseCase,
) : ViewModel() {

    private val _state = MutableStateFlow<StationSearchUiState>(StationSearchUiState.Loading)
    val state: StateFlow<StationSearchUiState> = _state

    fun handleEvent(event: StationSearchEvent) {
        when (event) {
            is StationSearchEvent.SearchByName -> searchByName(query = event.query)
        }
    }

    private fun searchByName(query: String) {
        viewModelScope.launch {
            // Implementation using searchStationsUseCase
        }
    }
}
```

**Key Points:**
- Always use `@HiltViewModel` annotation
- Always extend `ViewModel()` base class
- Inject dependencies via constructor with `@Inject`
- Use `viewModelScope` for coroutine operations
- Inject UseCases, not repositories (see `hilt-injection.md`)

Reference: [DetailStationViewModel.kt](file://../../../../feature/detail-station/src/main/java/com/gasguru/feature/detail_station/ui/DetailStationViewModel.kt)