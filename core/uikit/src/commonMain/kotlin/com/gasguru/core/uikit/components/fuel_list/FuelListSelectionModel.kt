package com.gasguru.core.uikit.components.fuel_list

import org.jetbrains.compose.resources.DrawableResource

data class FuelListSelectionModel(
    val list: List<FuelItemModel>,
    val selected: String?,
    val onItemSelected: (String) -> Unit,
)

data class FuelItemModel(
    val iconRes: DrawableResource,
    val nameRes: String,
)
