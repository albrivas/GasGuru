package com.gasguru.feature.detail_station.analytics

import com.gasguru.core.analytics.AnalyticsEvent
import com.gasguru.core.analytics.AnalyticsHelper

fun AnalyticsHelper.trackStationDetailViewed(brand: String, isFavorite: Boolean, hasPriceAlert: Boolean) {
    logEvent(
        AnalyticsEvent(
            type = AnalyticsEvent.Types.STATION_DETAIL_VIEWED,
            extras = listOf(
                AnalyticsEvent.Param(key = AnalyticsEvent.ParamKeys.STATION_BRAND, value = brand),
                AnalyticsEvent.Param(key = AnalyticsEvent.ParamKeys.IS_FAVORITE, value = isFavorite.toString()),
                AnalyticsEvent.Param(key = AnalyticsEvent.ParamKeys.HAS_PRICE_ALERT, value = hasPriceAlert.toString()),
            ),
        )
    )
}

fun AnalyticsHelper.trackStationFavorited(brand: String) {
    logEvent(
        AnalyticsEvent(
            type = AnalyticsEvent.Types.STATION_FAVORITED,
            extras = listOf(
                AnalyticsEvent.Param(key = AnalyticsEvent.ParamKeys.STATION_BRAND, value = brand),
            ),
        )
    )
}

fun AnalyticsHelper.trackStationUnfavorited(brand: String, source: String) {
    logEvent(
        AnalyticsEvent(
            type = AnalyticsEvent.Types.STATION_UNFAVORITED,
            extras = listOf(
                AnalyticsEvent.Param(key = AnalyticsEvent.ParamKeys.SOURCE, value = source),
                AnalyticsEvent.Param(key = AnalyticsEvent.ParamKeys.STATION_BRAND, value = brand),
            ),
        )
    )
}

fun AnalyticsHelper.trackStationShared(brand: String) {
    logEvent(
        AnalyticsEvent(
            type = AnalyticsEvent.Types.STATION_SHARED,
            extras = listOf(
                AnalyticsEvent.Param(key = AnalyticsEvent.ParamKeys.STATION_BRAND, value = brand),
            ),
        )
    )
}

fun AnalyticsHelper.trackPriceAlertEnabled() {
    logEvent(AnalyticsEvent(type = AnalyticsEvent.Types.PRICE_ALERT_ENABLED))
}

fun AnalyticsHelper.trackPriceAlertDisabled() {
    logEvent(AnalyticsEvent(type = AnalyticsEvent.Types.PRICE_ALERT_DISABLED))
}
