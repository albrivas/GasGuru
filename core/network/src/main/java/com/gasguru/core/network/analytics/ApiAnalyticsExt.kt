package com.gasguru.core.network.analytics

import com.gasguru.core.analytics.AnalyticsEvent
import com.gasguru.core.analytics.AnalyticsHelper

fun AnalyticsHelper.trackApiStationsFetchFailed(errorMessage: String, errorType: String) {
    logEvent(
        AnalyticsEvent(
            type = AnalyticsEvent.Types.API_STATIONS_FETCH_FAILED,
            extras = listOf(
                AnalyticsEvent.Param(key = AnalyticsEvent.ParamKeys.ERROR_MESSAGE, value = errorMessage),
                AnalyticsEvent.Param(key = AnalyticsEvent.ParamKeys.ERROR_TYPE, value = errorType),
            ),
        )
    )
}
