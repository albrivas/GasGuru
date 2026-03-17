package com.gasguru.core.analytics

import com.mixpanel.android.mpmetrics.MixpanelAPI
import org.json.JSONObject

class MixpanelAnalyticsHelper(private val mixpanel: MixpanelAPI) : AnalyticsHelper {

    override fun logEvent(event: AnalyticsEvent) {
        val properties = JSONObject()
        event.extras.forEach { param ->
            properties.put(param.key, param.value)
        }
        mixpanel.track(event.type, properties)
    }
}
