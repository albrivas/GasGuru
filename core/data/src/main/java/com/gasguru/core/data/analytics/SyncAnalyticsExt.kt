package com.gasguru.core.data.analytics

import com.gasguru.core.analytics.AnalyticsEvent
import com.gasguru.core.analytics.AnalyticsHelper

fun AnalyticsHelper.trackAlertsSyncFailed(errorMessage: String, errorType: String) {
    logEvent(
        AnalyticsEvent(
            type = AnalyticsEvent.Types.ALERTS_SYNC_FAILED,
            extras = listOf(
                AnalyticsEvent.Param(key = AnalyticsEvent.ParamKeys.ERROR_MESSAGE, value = errorMessage),
                AnalyticsEvent.Param(key = AnalyticsEvent.ParamKeys.ERROR_TYPE, value = errorType),
            ),
        )
    )
}
