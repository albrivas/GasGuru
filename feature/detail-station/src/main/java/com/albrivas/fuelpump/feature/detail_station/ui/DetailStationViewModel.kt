package com.albrivas.fuelpump.feature.detail_station.ui

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.albrivas.fuelpump.core.data.repository.LocationTracker
import com.albrivas.fuelpump.core.domain.GetFuelStationByIdUseCase
import com.albrivas.fuelpump.core.domain.RemoveFavoriteStationUseCase
import com.albrivas.fuelpump.core.domain.SaveFavoriteStationUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class DetailStationViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    getFuelStationByIdUseCase: GetFuelStationByIdUseCase,
    userLocation: LocationTracker,
    private val saveFavoriteStationUseCase: SaveFavoriteStationUseCase,
    private val removeFavoriteStationUseCase: RemoveFavoriteStationUseCase,
) : ViewModel() {

    private val id: Int = checkNotNull(savedStateHandle["idServiceStation"])

    @OptIn(ExperimentalCoroutinesApi::class)
    val fuelStation: StateFlow<DetailStationUiState> = userLocation.getCurrentLocationFlow
        .flatMapLatest { location ->
            location?.let {
                getFuelStationByIdUseCase(id = id, userLocation = location).map { station ->
                    DetailStationUiState.Success(station)
                }
            } ?: flowOf(DetailStationUiState.Error)
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = DetailStationUiState.Loading,
        )

    fun onFavoriteClick(isFavorite: Boolean) = viewModelScope.launch {
        if (isFavorite) {
            saveFavoriteStationUseCase(stationId = id)
        } else {
            removeFavoriteStationUseCase(stationId = id)
        }
    }
}
