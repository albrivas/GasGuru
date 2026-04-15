package com.gasguru.core.notifications

import com.gasguru.core.analytics.AnalyticsHelper

class PushNotificationServiceIos(
    @Suppress("UNUSED_PARAMETER") private val analyticsHelper: AnalyticsHelper,
) : NotificationService {

    override fun init() {
        // TODO Phase 4c V2: register OneSignal iOS click listener via SPM/cinterop.
        // When implemented, extract station_id from notification payload and track
        // analyticsHelper.trackPushNotificationTapped(notificationType = "price_alert")
    }
}
