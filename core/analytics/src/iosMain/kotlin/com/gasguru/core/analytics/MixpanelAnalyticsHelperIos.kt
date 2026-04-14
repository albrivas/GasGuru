package com.gasguru.core.analytics

import cocoapods.Mixpanel_swift.Mixpanel

class MixpanelAnalyticsHelperIos : AnalyticsHelper {

    override fun logEvent(event: AnalyticsEvent) {
        val properties = mutableMapOf<Any?, Any?>(
            AnalyticsEvent.ParamKeys.CATEGORY to event.category,
        )
        event.extras.forEach { param -> properties[param.key] = param.value }
        Mixpanel.mainInstance()?.track(event = event.type, properties = properties)
    }

    override fun updateSuperProperties(properties: Map<String, Any>) {
        val iosProperties = properties.entries
            .associate<Map.Entry<String, Any>, Any?, Any?> { (key, value) -> key to value }
            .toMutableMap()
        Mixpanel.mainInstance()?.registerSuperProperties(properties = iosProperties)
    }
}