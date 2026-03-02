package com.gasguru.feature.detail_station.ui

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.gasguru.core.domain.alerts.AddPriceAlertUseCase
import com.gasguru.core.domain.alerts.RemovePriceAlertUseCase
import com.gasguru.core.domain.fuelstation.GetFuelStationByIdUseCase
import com.gasguru.core.domain.fuelstation.RemoveFavoriteStationUseCase
import com.gasguru.core.domain.fuelstation.SaveFavoriteStationUseCase
import com.gasguru.core.domain.location.GetLastKnownLocationUseCase
import com.gasguru.core.domain.maps.GetStaticMapUrlUseCase
import com.gasguru.core.domain.places.GetAddressFromLocationUseCase
import com.gasguru.core.domain.user.GetUserDataUseCase
import com.gasguru.core.ui.mapper.toUiModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class DetailStationViewModel(
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
                    .flatMapLatest { station ->
                        getAddressFromLocationUseCase(
                            latitude = station.location.latitude,
                            longitude = station.location.longitude
                        ).map {
                            DetailStationUiState.Success(
                                stationModel = station.toUiModel(),
                                address = it
                            )
                        }.catch {
                            emit(
                                DetailStationUiState.Success(
                                    stationModel = station.toUiModel(),
                                    address = null
                                )
                            )
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

    @OptIn(ExperimentalCoroutinesApi::class)
    val staticMapUrl: StateFlow<String?> = fuelStation
        .flatMapLatest { uiState ->
            if (uiState is DetailStationUiState.Success) {
                flowOf(getStaticMapUrlUseCase(uiState.stationModel.fuelStation.location))
            } else {
                flowOf(null)
            }
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = null,
        )

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
            true -> {
                val userData = userDataUseCase().first()
                val station =
                    (fuelStation.value as DetailStationUiState.Success).stationModel.fuelStation
                val price = userData.vehicles.first().fuelType.extractPrice(station)

                addPriceAlertUseCase(stationId = id, lastNotifiedPrice = price)
            }

            false -> {
                removePriceAlertUseCase(stationId = id)
            }
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
