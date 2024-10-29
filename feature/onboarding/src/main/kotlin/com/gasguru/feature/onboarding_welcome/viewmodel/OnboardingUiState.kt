package com.gasguru.feature.onboarding_welcome.viewmodel

import com.gasguru.core.model.data.FuelType

sealed interface OnboardingUiState {
    data class ListFuelPreferences(val list: List<FuelType>) : OnboardingUiState
}
