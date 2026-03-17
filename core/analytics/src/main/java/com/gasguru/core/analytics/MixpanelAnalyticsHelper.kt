package com.gasguru.core.analytics

import android.content.Context
import com.mixpanel.android.mpmetrics.MixpanelAPI
import org.json.JSONObject

class MixpanelAnalyticsHelper(private val context: Context) : AnalyticsHelper {

    private val mixpanel: MixpanelAPI
        get() = MixpanelAPI.getInstance(context, null, true)

    override fun logEvent(event: AnalyticsEvent) {
        val properties = JSONObject()
        event.extras.forEach { param ->
            properties.put(param.key, param.value)
        }
        mixpanel.track(event.type, properties)
    }
}
