package com.gasguru.feature.detail_station.ui

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.gasguru.core.analytics.AnalyticsEvent
import com.gasguru.core.analytics.AnalyticsHelper
import com.gasguru.core.domain.alerts.AddPriceAlertUseCase
import com.gasguru.core.domain.alerts.RemovePriceAlertUseCase
import com.gasguru.core.domain.fuelstation.GetFuelStationByIdUseCase
import com.gasguru.core.domain.fuelstation.RemoveFavoriteStationUseCase
import com.gasguru.core.domain.fuelstation.SaveFavoriteStationUseCase
import com.gasguru.core.domain.location.GetLastKnownLocationUseCase
import com.gasguru.core.domain.maps.GetStaticMapUrlUseCase
import com.gasguru.core.domain.places.GetAddressFromLocationUseCase
import com.gasguru.core.domain.user.GetUserDataUseCase
import com.gasguru.core.domain.vehicle.UpdateVehicleTankCapacityUseCase
import com.gasguru.core.model.data.Vehicle
import com.gasguru.core.model.data.principalVehicle
import com.gasguru.core.common.CommonUtils.isStationOpen
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
    private val updateVehicleTankCapacityUseCase: UpdateVehicleTankCapacityUseCase,
    private val analyticsHelper: AnalyticsHelper,
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
                                address = it,
                                isOpen = station.isStationOpen(),
                            )
                        }.catch {
                            emit(
                                DetailStationUiState.Success(
                                    stationModel = station.toUiModel(),
                                    address = null,
                                    isOpen = station.isStationOpen(),
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

            is DetailStationEvent.UpdateTankCapacity -> {
                onUpdateTankCapacity(event.capacity)
            }

            DetailStationEvent.ShareStation -> onShareStation()
        }
    }

    private fun onFavoriteClick(isFavorite: Boolean) = viewModelScope.launch {
        when (isFavorite) {
            true -> {
                analyticsHelper.logEvent(
                    event = AnalyticsEvent(
                        type = AnalyticsEvent.Types.STATION_FAVORITED,
                        extras = listOf(
                            AnalyticsEvent.Param(key = AnalyticsEvent.ParamKeys.STATION_ID, value = id.toString()),
                        ),
                    ),
                )
                saveFavoriteStationUseCase(stationId = id)
            }
            false -> {
                analyticsHelper.logEvent(
                    event = AnalyticsEvent(
                        type = AnalyticsEvent.Types.STATION_UNFAVORITED,
                        extras = listOf(
                            AnalyticsEvent.Param(key = AnalyticsEvent.ParamKeys.STATION_ID, value = id.toString()),
                        ),
                    ),
                )
                removeFavoriteStationUseCase(stationId = id)
            }
        }
    }

    private fun onUpdateTankCapacity(tankCapacity: Int) = viewModelScope.launch {
        val currentVehicle = vehicle.value ?: return@launch
        updateVehicleTankCapacityUseCase(vehicleId = currentVehicle.id, tankCapacity = tankCapacity)
    }

    private fun onShareStation() {
        analyticsHelper.logEvent(
            event = AnalyticsEvent(
                type = AnalyticsEvent.Types.STATION_SHARED,
                extras = listOf(
                    AnalyticsEvent.Param(key = AnalyticsEvent.ParamKeys.STATION_ID, value = id.toString()),
                ),
            ),
        )
    }

    private fun onPriceAlertClick(isEnabled: Boolean) = viewModelScope.launch {
        when (isEnabled) {
            true -> {
                analyticsHelper.logEvent(
                    event = AnalyticsEvent(
                        type = AnalyticsEvent.Types.PRICE_ALERT_ENABLED,
                        extras = listOf(
                            AnalyticsEvent.Param(key = AnalyticsEvent.ParamKeys.STATION_ID, value = id.toString()),
                        ),
                    ),
                )
                val userData = userDataUseCase().first()
                val station =
                    (fuelStation.value as DetailStationUiState.Success).stationModel.fuelStation
                val price = userData.principalVehicle().fuelType.extractPrice(station)

                addPriceAlertUseCase(stationId = id, lastNotifiedPrice = price)
            }

            false -> {
                analyticsHelper.logEvent(
                    event = AnalyticsEvent(
                        type = AnalyticsEvent.Types.PRICE_ALERT_DISABLED,
                        extras = listOf(
                            AnalyticsEvent.Param(key = AnalyticsEvent.ParamKeys.STATION_ID, value = id.toString()),
                        ),
                    ),
                )
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

    val vehicle: StateFlow<Vehicle?> = userDataUseCase().map {
        it.vehicles.firstOrNull()
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = null,
    )
}
