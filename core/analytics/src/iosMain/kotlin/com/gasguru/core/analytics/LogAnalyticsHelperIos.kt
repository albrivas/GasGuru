package com.gasguru.core.analytics

import platform.Foundation.NSLog

class LogAnalyticsHelperIos : AnalyticsHelper {

    override fun logEvent(event: AnalyticsEvent) {
        val paramsString = event.extras.joinToString(separator = ", ") { "${it.key}=${it.value}" }
        NSLog("▶ [${event.category}] ${event.type} | $paramsString")
    }

    override fun updateSuperProperties(properties: Map<String, Any>) {
        val propsString = properties.entries.joinToString(separator = ", ") { "${it.key}=${it.value}" }
        NSLog("⚙ [super_properties] $propsString")
    }
}
