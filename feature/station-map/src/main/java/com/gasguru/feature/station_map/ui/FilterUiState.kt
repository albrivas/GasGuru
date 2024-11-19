package com.gasguru.feature.station_map.ui

import android.content.Context
import com.gasguru.feature.station_map.R

data class FilterUiState(
    val filterBrand: List<String> = emptyList(),
    val filterStationsNearby: Int = 10,
    val filterSchedule: OpeningHours = OpeningHours.NONE,
) {
    enum class OpeningHours(val resId: Int) {
        NONE(R.string.filter_schedule),
        OPEN_NOW(R.string.filter_open_now),
        OPEN_24_H(R.string.filter_open_24);

        companion object {
            fun fromTranslatedString(translatedString: String, context: Context): OpeningHours {
                return entries.find { context.getString(it.resId) == translatedString } ?: NONE
            }
        }
    }
}
