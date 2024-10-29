package com.gasguru.feature.onboarding_welcome.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.gasguru.core.domain.SaveUserDataUseCase
import com.gasguru.core.model.data.FuelType
import com.gasguru.core.model.data.UserData
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class OnboardingViewModel @Inject constructor(
    private val saveUserDataUseCase: SaveUserDataUseCase
) : ViewModel() {

    var state by mutableStateOf(OnboardingUiState.ListFuelPreferences(listOf()))
        private set

    init {
        getFuelList()
    }

    private fun getFuelList() {
        listOf(
            FuelType.GASOLINE_95,
            FuelType.GASOLINE_98,
            FuelType.DIESEL,
            FuelType.DIESEL_PLUS
        ).also { state = OnboardingUiState.ListFuelPreferences(it) }
    }

    fun saveSelectedFuel(selectedFuel: FuelType) {
        viewModelScope.launch {
            saveUserDataUseCase(UserData(fuelSelection = selectedFuel))
        }
    }
}
