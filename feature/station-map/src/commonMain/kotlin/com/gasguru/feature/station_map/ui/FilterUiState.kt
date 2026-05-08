package com.gasguru.feature.station_map.ui

data class FilterUiState(
    val filterBrand: List<String> = emptyList(),
    val filterStationsNearby: Int = 10,
    val filterSchedule: OpeningHours = OpeningHours.NONE,
) {
    enum class OpeningHours {
        NONE, OPEN_NOW, OPEN_24_H;

        companion object {
            fun fromName(name: String?): OpeningHours =
                entries.firstOrNull { it.name == name } ?: NONE
        }
    }
}
