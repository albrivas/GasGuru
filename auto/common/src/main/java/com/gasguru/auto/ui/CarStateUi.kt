package com.gasguru.auto.ui

import com.gasguru.core.model.data.FuelStation
import com.gasguru.core.model.data.FuelType

data class CarUiState(
    val loading: Boolean = false,
    val stations: List<FuelStation> = emptyList(),
    val selectedFuel: FuelType? = null,
)