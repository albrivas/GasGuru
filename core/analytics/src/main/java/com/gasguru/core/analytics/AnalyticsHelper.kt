package com.gasguru.core.analytics

interface AnalyticsHelper {
    fun logEvent(event: AnalyticsEvent)
    fun updateSuperProperties(properties: Map<String, Any>)
}
