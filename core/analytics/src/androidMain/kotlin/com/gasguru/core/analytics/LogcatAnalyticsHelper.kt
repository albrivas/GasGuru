package com.gasguru.core.analytics

import android.util.Log

private const val TAG = "Analytics"

class LogcatAnalyticsHelper : AnalyticsHelper {

    override fun logEvent(event: AnalyticsEvent) {
        val paramsString = if (event.extras.isEmpty()) {
            "—"
        } else {
            event.extras.joinToString(separator = ", ") { param -> "${param.key}=${param.value}" }
        }
        Log.d(TAG, "▶ [${event.category}] ${event.type} | $paramsString")
    }

    override fun updateSuperProperties(properties: Map<String, Any>) {
        val propsString = properties.entries.joinToString(separator = ", ") { (key, value) -> "$key=$value" }
        Log.d(TAG, "⚙ [super_properties] $propsString")
    }
}
