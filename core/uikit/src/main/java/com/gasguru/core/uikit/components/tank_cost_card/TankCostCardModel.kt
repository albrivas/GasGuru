package com.gasguru.core.uikit.components.tank_cost_card

import com.gasguru.core.uikit.components.fuel_type_chip.FuelTypeChipModel

data class TankCostCardModel(
    val fuelTypeChip: FuelTypeChipModel,
    val totalCost: String,
    val litres: String,
    val pricePerLitre: String,
    val vehicleName: String,
    val onEditClick: () -> Unit = {},
)
