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
        Log.d(TAG, "▶ ${event.type} | $paramsString")
    }
}
