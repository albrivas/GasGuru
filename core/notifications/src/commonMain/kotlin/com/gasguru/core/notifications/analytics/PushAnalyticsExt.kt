package com.gasguru.core.notifications.analytics

import com.gasguru.core.analytics.AnalyticsEvent
import com.gasguru.core.analytics.AnalyticsHelper

fun AnalyticsHelper.trackPushNotificationTapped(notificationType: String) {
    logEvent(
        AnalyticsEvent(
            type = AnalyticsEvent.Types.PUSH_NOTIFICATION_TAPPED,
            extras = listOf(
                AnalyticsEvent.Param(key = AnalyticsEvent.ParamKeys.NOTIFICATION_TYPE, value = notificationType),
            ),
        )
    )
}
