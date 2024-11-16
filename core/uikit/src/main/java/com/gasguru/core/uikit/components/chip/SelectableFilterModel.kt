package com.gasguru.core.uikit.components.chip

sealed class FilterType {
    data object Brand : FilterType()
    data object Schedule : FilterType()
    data object NumberOfStations : FilterType()
}

data class SelectableFilterModel(
    val filterType: FilterType,
    val label: String,
    val selectedLabel: String,
    val isSelected: Boolean,
    val selectedCount: Int? = null,
    val onFilterClick: (FilterType) -> Unit = {}
)
