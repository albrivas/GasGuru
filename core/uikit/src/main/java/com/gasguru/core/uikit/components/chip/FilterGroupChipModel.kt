package com.gasguru.core.uikit.components.chip

data class FilterOption(
    val label: String,
    var isSelected: Boolean = false
)

data class Filter(
    val options: List<FilterOption>,
    val isMultiSelect: Boolean = false,
    val type: FilterType
)

sealed class FilterType {
    data object Brand : FilterType()
    data object Schedule : FilterType()
    data object NumberOfStations : FilterType()
}

/**
 * Data model for the `FilterChipGroup` composable. Represents a group of filters,
 * where each filter can have multiple selectable options.
 *
 * @param filters List of filters. Each filter is represented by the `Filter` class.
 * @param onFilterSelected Callback invoked when a filter option is selected or deselected.
 * Receives three parameters:
 *   - filterType: Filter type selected ([FilterType]).
 */
data class FilterGroupChipModel(
    val filters: List<Filter>,
    val onFilterSelected: (FilterType) -> Unit // filterType, optionIndex, isSelected
)
