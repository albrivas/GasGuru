package com.gasguru.feature.route_planner.analytics

import com.gasguru.core.analytics.AnalyticsEvent
import com.gasguru.core.analytics.AnalyticsHelper

fun AnalyticsHelper.trackRoutePlannerDestinationSet(isCurrentLocation: Boolean) {
    logEvent(
        AnalyticsEvent(
            type = AnalyticsEvent.Types.ROUTE_PLANNER_DESTINATION_SET,
            extras = listOf(
                AnalyticsEvent.Param(key = AnalyticsEvent.ParamKeys.IS_CURRENT_LOCATION, value = isCurrentLocation.toString()),
            ),
        )
    )
}

fun AnalyticsHelper.trackRoutePlannerDestinationsSwapped() {
    logEvent(AnalyticsEvent(type = AnalyticsEvent.Types.ROUTE_PLANNER_DESTINATIONS_SWAPPED))
}

fun AnalyticsHelper.trackRecentSearchUsed() {
    logEvent(AnalyticsEvent(type = AnalyticsEvent.Types.RECENT_SEARCH_USED))
}
