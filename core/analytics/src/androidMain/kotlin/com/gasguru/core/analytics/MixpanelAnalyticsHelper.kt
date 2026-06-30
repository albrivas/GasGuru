package com.gasguru.core.analytics

import com.mixpanel.android.mpmetrics.MixpanelAPI
import org.json.JSONObject

class MixpanelAnalyticsHelper(
    private val mixpanel: MixpanelAPI,
) : AnalyticsHelper {

    override fun logEvent(event: AnalyticsEvent) {
        val properties = JSONObject()
        properties.put(AnalyticsEvent.ParamKeys.CATEGORY, event.category)
        event.extras.forEach { param ->
            properties.put(param.key, param.value)
        }
        mixpanel.track(event.type, properties)
    }

    override fun updateSuperProperties(properties: Map<String, Any>) {
        val jsonProperties = JSONObject()
        properties.forEach { (key, value) -> jsonProperties.put(key, value) }
        mixpanel.registerSuperProperties(jsonProperties)
    }
}
