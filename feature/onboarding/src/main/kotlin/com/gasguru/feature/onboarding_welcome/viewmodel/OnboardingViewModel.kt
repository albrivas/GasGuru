package com.gasguru.feature.onboarding_welcome.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.gasguru.core.domain.GetUserDataUseCase
import com.gasguru.core.domain.SaveUserDataUseCase
import com.gasguru.core.model.data.FuelType
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class OnboardingViewModel @Inject constructor(
    private val saveUserDataUseCase: SaveUserDataUseCase,
    private val userDataUseCase: GetUserDataUseCase,
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
            val user = userDataUseCase().first()
            saveUserDataUseCase(user.copy(fuelSelection = selectedFuel))
        }
    }
}
