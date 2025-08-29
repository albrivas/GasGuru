package com.gasguru.auto.ui

import com.gasguru.core.model.data.FuelType
import com.gasguru.core.ui.models.FuelStationUiModel

data class CarUiState(
    val loading: Boolean = false,
    val stations: List<FuelStationUiModel> = emptyList(),
    val selectedFuel: FuelType? = null,
    val permissionDenied: Boolean = true,
    val needsOnboarding: Boolean = false,
    val locationDisabled: Boolean = false,
)
