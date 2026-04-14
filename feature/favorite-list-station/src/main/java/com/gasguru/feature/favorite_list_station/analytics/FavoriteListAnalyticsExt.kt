package com.gasguru.feature.favorite_list_station.analytics

import com.gasguru.core.analytics.AnalyticsEvent
import com.gasguru.core.analytics.AnalyticsHelper

fun AnalyticsHelper.trackStationUnfavoritedFromList(brand: String) {
    logEvent(
        AnalyticsEvent(
            type = AnalyticsEvent.Types.STATION_UNFAVORITED,
            extras = listOf(
                AnalyticsEvent.Param(key = AnalyticsEvent.ParamKeys.SOURCE, value = "list"),
                AnalyticsEvent.Param(key = AnalyticsEvent.ParamKeys.STATION_BRAND, value = brand),
            ),
        )
    )
}
