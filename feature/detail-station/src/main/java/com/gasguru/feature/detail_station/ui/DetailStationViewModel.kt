package com.gasguru.feature.detail_station.ui

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.gasguru.core.domain.GetAddressFromLocationUseCase
import com.gasguru.core.domain.GetFuelStationByIdUseCase
import com.gasguru.core.domain.GetUserDataUseCase
import com.gasguru.core.domain.RemoveFavoriteStationUseCase
import com.gasguru.core.domain.SaveFavoriteStationUseCase
import com.gasguru.core.domain.history.GetHistoryByFuelUseCase
import com.gasguru.core.domain.location.GetLastKnownLocationUseCase
import com.gasguru.core.model.data.FuelStation
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.LocalDate
import javax.inject.Inject

@HiltViewModel
class DetailStationViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val getFuelStationByIdUseCase: GetFuelStationByIdUseCase,
    private val getLastKnownLocationUseCase: GetLastKnownLocationUseCase,
    private val userDataUseCase: GetUserDataUseCase,
    private val saveFavoriteStationUseCase: SaveFavoriteStationUseCase,
    private val removeFavoriteStationUseCase: RemoveFavoriteStationUseCase,
    private val getAddressFromLocationUseCase: GetAddressFromLocationUseCase,
    private val getHistoryByFuelUseCase: GetHistoryByFuelUseCase,
) : ViewModel() {

    private val id: Int = checkNotNull(savedStateHandle["idServiceStation"])

    private val _detailUiState =
        MutableStateFlow<DetailStationUiState>(DetailStationUiState.Loading)
    val detailUiState = _detailUiState.asStateFlow()

    private val _historyUiState = MutableStateFlow<PriceHistoryUiState>(PriceHistoryUiState.Loading)
    val historyUiState = _historyUiState.asStateFlow()

    init {
        getDetailStation()
    }

    private fun getDetailStation() = viewModelScope.launch {
        getLastKnownLocationUseCase().collectLatest { location ->
            location?.let { safeLocation ->
                getFuelStationByIdUseCase(id = id, userLocation = safeLocation)
                    .collectLatest { station ->
                        getHistory(station)
                        getAddressFromLocationUseCase(
                            latitude = station.location.latitude,
                            longitude = station.location.longitude
                        ).collectLatest { address ->
                            _detailUiState.update {
                                DetailStationUiState.Success(
                                    station = station,
                                    address = address
                                )
                            }
                        }
                    }
            } ?: _detailUiState.update { DetailStationUiState.Error }
        }
    }

    private fun getHistory(fuelStation: FuelStation) = viewModelScope.launch {
        userDataUseCase().collectLatest {
            getHistoryByFuelUseCase(
                idStation = id,
                idMunicipality = fuelStation.idMunicipality,
                idProduct = it.fuelSelection.idProduct,
                date = LocalDate.now()
            ).catch { _historyUiState.update { PriceHistoryUiState.Error } }
                .collect { prices -> _historyUiState.update { PriceHistoryUiState.Success(prices) } }
        }
    }

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
