package com.gasguru.analytics

import com.gasguru.core.analytics.AnalyticsEvent
import com.gasguru.core.analytics.AnalyticsHelper

fun AnalyticsHelper.trackStationSyncWorkerRetried(errorMessage: String, errorType: String) {
    logEvent(
        AnalyticsEvent(
            type = AnalyticsEvent.Types.STATION_SYNC_WORKER_RETRIED,
            extras = listOf(
                AnalyticsEvent.Param(key = AnalyticsEvent.ParamKeys.ERROR_MESSAGE, value = errorMessage),
                AnalyticsEvent.Param(key = AnalyticsEvent.ParamKeys.ERROR_TYPE, value = errorType),
            ),
        )
    )
}
