package com.gasguru.core.analytics

import android.content.Context
import com.mixpanel.android.mpmetrics.MixpanelAPI
import org.json.JSONObject

class MixpanelAnalyticsHelper(
    private val context: Context,
    private val mixpanel: MixpanelAPI,
) : AnalyticsHelper {

    init {
        val appVersion = runCatching {
            context.packageManager.getPackageInfo(context.packageName, 0).versionName.orEmpty()
        }.getOrDefault("")

        mixpanel.registerSuperPropertiesOnce(
            JSONObject().apply {
                put("app_version", appVersion)
                put("platform", "android")
            }
        )
    }

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
