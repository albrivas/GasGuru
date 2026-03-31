package com.gasguru.feature.onboarding_welcome.viewmodel

import com.gasguru.core.model.data.FuelType

data class OnboardingUiState(
    val fuelList: List<FuelType> = FuelType.entries.toList(),
    val isButtonEnabled: Boolean = false,
    val selectedFuel: FuelType? = null
)
