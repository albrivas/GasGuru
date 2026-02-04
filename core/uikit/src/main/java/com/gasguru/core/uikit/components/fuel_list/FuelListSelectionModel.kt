package com.gasguru.core.uikit.components.fuel_list

data class FuelListSelectionModel(
    val list: List<FuelItemModel>,
    val selected: Int?,
    val onItemSelected: (Int) -> Unit
)

data class FuelItemModel(
    val iconRes: Int,
    val nameRes: Int,
)
