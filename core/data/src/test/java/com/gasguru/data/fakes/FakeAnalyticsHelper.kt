package com.gasguru.data.fakes

import com.gasguru.core.analytics.AnalyticsEvent
import com.gasguru.core.analytics.AnalyticsHelper

class FakeAnalyticsHelper : AnalyticsHelper {
    val loggedEvents = mutableListOf<AnalyticsEvent>()

    override fun logEvent(event: AnalyticsEvent) {
        loggedEvents.add(event)
    }
}
