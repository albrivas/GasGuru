package com.gasguru.feature.detail_station.ui

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.gasguru.core.domain.fuelstation.GetFuelStationByIdUseCase
import com.gasguru.core.domain.fuelstation.RemoveFavoriteStationUseCase
import com.gasguru.core.domain.fuelstation.SaveFavoriteStationUseCase
import com.gasguru.core.domain.location.GetLastKnownLocationUseCase
import com.gasguru.core.domain.places.GetAddressFromLocationUseCase
import com.gasguru.core.domain.user.GetUserDataUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
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
    getLastKnownLocationUseCase: GetLastKnownLocationUseCase,
    userDataUseCase: GetUserDataUseCase,
    private val saveFavoriteStationUseCase: SaveFavoriteStationUseCase,
    private val removeFavoriteStationUseCase: RemoveFavoriteStationUseCase,
    private val getAddressFromLocationUseCase: GetAddressFromLocationUseCase,
) : ViewModel() {

    private val id: Int = checkNotNull(savedStateHandle["idServiceStation"])

    @OptIn(ExperimentalCoroutinesApi::class)
    val fuelStation: StateFlow<DetailStationUiState> = getLastKnownLocationUseCase()
        .flatMapLatest { location ->
            location?.let { safeLocation ->
                getFuelStationByIdUseCase(id = id, userLocation = safeLocation)
                    .flatMapLatest { station ->
                        getAddressFromLocationUseCase(
                            latitude = station.location.latitude,
                            longitude = station.location.longitude
                        ).map {
                            DetailStationUiState.Success(station = station, address = it)
                        }
                    }.catch {
                        DetailStationUiState.Error
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

    val lastUpdate: StateFlow<Long> = userDataUseCase().map {
        it.lastUpdate
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = 0L,
    )
}
