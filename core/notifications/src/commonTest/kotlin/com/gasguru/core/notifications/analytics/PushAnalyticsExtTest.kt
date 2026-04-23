package com.gasguru.core.notifications.analytics

import com.gasguru.core.analytics.AnalyticsEvent
import com.gasguru.core.analytics.AnalyticsHelper
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class PushAnalyticsExtTest {

    private val fakeAnalyticsHelper = FakeAnalyticsHelper()

    @Test
    fun givenNotificationType_whenTrackPushNotificationTapped_thenLogsCorrectEvent() {
        fakeAnalyticsHelper.trackPushNotificationTapped(notificationType = "price_alert")

        assertTrue(fakeAnalyticsHelper.loggedEvents.isNotEmpty())
        val event = fakeAnalyticsHelper.loggedEvents.first()
        assertEquals(expected = AnalyticsEvent.Types.PUSH_NOTIFICATION_TAPPED, actual = event.type)
    }

    @Test
    fun givenNotificationType_whenTrackPushNotificationTapped_thenIncludesNotificationTypeParam() {
        fakeAnalyticsHelper.trackPushNotificationTapped(notificationType = "price_alert")

        val event = fakeAnalyticsHelper.loggedEvents.first()
        val param = event.extras.find { it.key == AnalyticsEvent.ParamKeys.NOTIFICATION_TYPE }
        assertEquals(expected = "price_alert", actual = param?.value)
    }
}

private class FakeAnalyticsHelper : AnalyticsHelper {
    val loggedEvents = mutableListOf<AnalyticsEvent>()

    override fun logEvent(event: AnalyticsEvent) {
        loggedEvents.add(event)
    }

    override fun updateSuperProperties(properties: Map<String, Any>) = Unit
}
