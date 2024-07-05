package com.albrivas.fuelpump.feature.onboarding_welcome.viewmodel

import com.albrivas.fuelpump.core.model.data.FuelType

sealed interface OnboardingUiState {
    data class ListFuelPreferences(val list: List<FuelType>) : OnboardingUiState
}
