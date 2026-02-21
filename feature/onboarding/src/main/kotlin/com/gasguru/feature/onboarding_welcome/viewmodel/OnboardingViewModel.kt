package com.gasguru.feature.onboarding_welcome.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.gasguru.core.domain.fuelstation.SaveFuelSelectionUseCase
import com.gasguru.core.model.data.FuelType
import kotlinx.coroutines.launch

class OnboardingViewModel constructor(
    private val saveFuelSelectionUseCase: SaveFuelSelectionUseCase
) : ViewModel() {

    var state by mutableStateOf(OnboardingUiState.ListFuelPreferences(listOf()))
        private set

    init {
        getFuelList()
    }

    private fun getFuelList() {
        FuelType.entries.toList()
            .also { state = OnboardingUiState.ListFuelPreferences(it) }
    }

    fun saveSelectedFuel(selectedFuel: FuelType) {
        viewModelScope.launch {
            saveFuelSelectionUseCase(fuelType = selectedFuel)
        }
    }
}
