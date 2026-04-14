package com.gasguru.feature.station_map.analytics

import com.gasguru.core.analytics.AnalyticsEvent
import com.gasguru.core.analytics.AnalyticsHelper

fun AnalyticsHelper.trackStationSelected(brand: String, source: String = "map") {
    logEvent(
        AnalyticsEvent(
            type = AnalyticsEvent.Types.STATION_SELECTED,
            extras = listOf(
                AnalyticsEvent.Param(key = AnalyticsEvent.ParamKeys.SELECTION_SOURCE, value = source),
                AnalyticsEvent.Param(key = AnalyticsEvent.ParamKeys.STATION_BRAND, value = brand),
            ),
        )
    )
}

fun AnalyticsHelper.trackStationSelectedWithoutBrand(source: String = "map") {
    logEvent(
        AnalyticsEvent(
            type = AnalyticsEvent.Types.STATION_SELECTED,
            extras = listOf(
                AnalyticsEvent.Param(key = AnalyticsEvent.ParamKeys.SELECTION_SOURCE, value = source),
            ),
        )
    )
}

fun AnalyticsHelper.trackFilterBrandChanged(brandCount: Int, brandNames: String) {
    logEvent(
        AnalyticsEvent(
            type = AnalyticsEvent.Types.FILTER_BRAND_CHANGED,
            extras = listOf(
                AnalyticsEvent.Param(key = AnalyticsEvent.ParamKeys.BRAND_COUNT, value = brandCount.toString()),
                AnalyticsEvent.Param(key = AnalyticsEvent.ParamKeys.BRAND_NAMES, value = brandNames),
            ),
        )
    )
}

fun AnalyticsHelper.trackFilterNearbyChanged(nearbyKm: String) {
    logEvent(
        AnalyticsEvent(
            type = AnalyticsEvent.Types.FILTER_NEARBY_CHANGED,
            extras = listOf(
                AnalyticsEvent.Param(key = AnalyticsEvent.ParamKeys.NEARBY_KM, value = nearbyKm),
            ),
        )
    )
}

fun AnalyticsHelper.trackFilterScheduleChanged(schedule: String) {
    logEvent(
        AnalyticsEvent(
            type = AnalyticsEvent.Types.FILTER_SCHEDULE_CHANGED,
            extras = listOf(
                AnalyticsEvent.Param(key = AnalyticsEvent.ParamKeys.SCHEDULE, value = schedule),
            ),
        )
    )
}

fun AnalyticsHelper.trackMapTabChanged(tab: String) {
    logEvent(
        AnalyticsEvent(
            type = AnalyticsEvent.Types.MAP_TAB_CHANGED,
            extras = listOf(
                AnalyticsEvent.Param(key = AnalyticsEvent.ParamKeys.TAB, value = tab),
            ),
        )
    )
}

fun AnalyticsHelper.trackRouteStarted(brand: String?) {
    logEvent(
        AnalyticsEvent(
            type = AnalyticsEvent.Types.ROUTE_STARTED,
            extras = buildList {
                brand?.let { add(AnalyticsEvent.Param(key = AnalyticsEvent.ParamKeys.STATION_BRAND, value = it)) }
            },
        )
    )
}

fun AnalyticsHelper.trackRouteCancelled(brand: String?) {
    logEvent(
        AnalyticsEvent(
            type = AnalyticsEvent.Types.ROUTE_CANCELLED,
            extras = buildList {
                brand?.let { add(AnalyticsEvent.Param(key = AnalyticsEvent.ParamKeys.STATION_BRAND, value = it)) }
            },
        )
    )
}
