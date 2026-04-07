package com.gasguru.core.testing.fakes.analytics

import com.gasguru.core.analytics.AnalyticsEvent
import com.gasguru.core.analytics.AnalyticsHelper

class FakeAnalyticsHelper : AnalyticsHelper {
    val loggedEvents = mutableListOf<AnalyticsEvent>()

    override fun logEvent(event: AnalyticsEvent) {
        loggedEvents.add(event)
    }

    override fun updateSuperProperties(properties: Map<String, Any>) = Unit

    fun hasEvent(type: String): Boolean = loggedEvents.any { it.type == type }

    fun eventsOfType(type: String): List<AnalyticsEvent> = loggedEvents.filter { it.type == type }

    fun reset() = loggedEvents.clear()
}
