package com.gasguru.core.analytics

class NoOpAnalyticsHelper : AnalyticsHelper {
    override fun logEvent(event: AnalyticsEvent) = Unit
    override fun updateSuperProperties(properties: Map<String, Any>) = Unit
}
