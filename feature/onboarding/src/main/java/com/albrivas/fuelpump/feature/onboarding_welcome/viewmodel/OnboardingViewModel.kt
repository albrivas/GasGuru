package com.albrivas.fuelpump.feature.onboarding_welcome.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import javax.inject.Inject
import dagger.hilt.android.lifecycle.HiltViewModel
import androidx.lifecycle.ViewModel
import com.albrivas.fuelpump.core.model.data.FuelType

@HiltViewModel
class OnboardingViewModel @Inject constructor(

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
            FuelType.DIESEL_PLUS,
            FuelType.ELECTRIC
        ).also { state = OnboardingUiState.ListFuelPreferences(it) }
    }
}