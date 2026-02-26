package com.gasguru.feature.onboarding_welcome.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.gasguru.core.domain.fuelstation.SaveFuelSelectionUseCase
import com.gasguru.core.model.data.FuelType
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class OnboardingViewModel(
    private val saveFuelSelectionUseCase: SaveFuelSelectionUseCase
) : ViewModel() {

    private val _state = MutableStateFlow(OnboardingUiState())
    val state = _state.asStateFlow()

    fun selectedFuel(fuelType: FuelType) = viewModelScope.launch {
        _state.update { it.copy(selectedFuel = fuelType) }
    }

    fun saveSelectedFuel(selectedFuel: FuelType) {
        viewModelScope.launch {
            saveFuelSelectionUseCase(fuelType = selectedFuel)
        }
    }
}
