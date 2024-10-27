package com.albrivas.fuelpump.core.uikit.fuel_list

data class FuelListSelectionModel(
    val list: List<Pair<Int, Int>>,
    val selected: Int?,
    val onItemSelected: (Int) -> Unit
)
