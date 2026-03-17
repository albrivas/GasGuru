package com.gasguru.feature.onboarding_welcome.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.gasguru.core.analytics.AnalyticsEvent
import com.gasguru.core.analytics.AnalyticsHelper
import com.gasguru.core.domain.vehicle.SaveDefaultVehicleFuelTypeUseCase
import com.gasguru.core.model.data.FuelType
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class OnboardingViewModel(
    private val saveDefaultVehicleFuelTypeUseCase: SaveDefaultVehicleFuelTypeUseCase,
    private val analyticsHelper: AnalyticsHelper,
) : ViewModel() {

    private val _state = MutableStateFlow(OnboardingUiState())
    val state = _state.asStateFlow()

    fun selectedFuel(fuelType: FuelType) = viewModelScope.launch {
        _state.update { it.copy(selectedFuel = fuelType) }
    }

    fun saveSelectedFuel(selectedFuel: FuelType) {
        analyticsHelper.logEvent(
            event = AnalyticsEvent(
                type = AnalyticsEvent.Types.ONBOARDING_FUEL_SELECTED,
                extras = listOf(
                    AnalyticsEvent.Param(key = AnalyticsEvent.ParamKeys.FUEL_TYPE, value = selectedFuel.name),
                ),
            ),
        )
        viewModelScope.launch {
            saveDefaultVehicleFuelTypeUseCase(fuelType = selectedFuel)
        }
    }
}
