package com.gasguru.analytics

import com.gasguru.core.analytics.AnalyticsEvent
import com.gasguru.core.analytics.AnalyticsHelper

fun AnalyticsHelper.trackAppOpened(source: String) {
    logEvent(
        AnalyticsEvent(
            type = AnalyticsEvent.Types.APP_OPENED,
            extras = listOf(
                AnalyticsEvent.Param(key = AnalyticsEvent.ParamKeys.SOURCE, value = source),
            ),
        )
    )
}
