package com.gasguru.auto.analytics

import com.gasguru.core.analytics.AnalyticsEvent
import com.gasguru.core.analytics.AnalyticsHelper

fun AnalyticsHelper.trackAutoSessionStarted() {
    logEvent(AnalyticsEvent(type = AnalyticsEvent.Types.AUTO_SESSION_STARTED))
}

fun AnalyticsHelper.trackAutoNearbyStationsOpened() {
    logEvent(AnalyticsEvent(type = AnalyticsEvent.Types.AUTO_NEARBY_STATIONS_OPENED))
}

fun AnalyticsHelper.trackAutoFavoriteStationsOpened() {
    logEvent(AnalyticsEvent(type = AnalyticsEvent.Types.AUTO_FAVORITE_STATIONS_OPENED))
}

fun AnalyticsHelper.trackAutoStationNavigationStarted(brand: String) {
    logEvent(
        AnalyticsEvent(
            type = AnalyticsEvent.Types.AUTO_STATION_NAVIGATION_STARTED,
            extras = listOf(
                AnalyticsEvent.Param(key = AnalyticsEvent.ParamKeys.STATION_BRAND, value = brand),
            ),
        )
    )
}
