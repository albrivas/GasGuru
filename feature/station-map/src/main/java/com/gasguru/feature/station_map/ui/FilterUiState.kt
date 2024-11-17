package com.gasguru.feature.station_map.ui

data class FilterUiState(
    val filterBrand: List<String> = emptyList(),
    val filterStationsNearby: Int = 10,
    val filterSchedule: OpeningHours = OpeningHours.NONE,
)

enum class OpeningHours(value: Int) {
    NONE(0), OPEN_NOW(1), OPEN_24_H(2)
}
