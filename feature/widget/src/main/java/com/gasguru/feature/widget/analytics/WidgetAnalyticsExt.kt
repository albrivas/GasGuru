package com.gasguru.feature.widget.analytics

import com.gasguru.core.analytics.AnalyticsEvent
import com.gasguru.core.analytics.AnalyticsHelper

fun AnalyticsHelper.trackWidgetStationTapped() {
    logEvent(AnalyticsEvent(type = AnalyticsEvent.Types.WIDGET_STATION_TAPPED))
}

fun AnalyticsHelper.trackWidgetAddedToHome() {
    logEvent(AnalyticsEvent(type = AnalyticsEvent.Types.WIDGET_ADDED_TO_HOME))
}
