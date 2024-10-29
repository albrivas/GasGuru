package com.gasguru.core.uikit.components.chip

data class FilterChipModel(
    val options: List<String>,
    val selectedChip: Int = 0,
    val enabled: Boolean = true,
    val onFilterSelected: (Int) -> Unit
)
