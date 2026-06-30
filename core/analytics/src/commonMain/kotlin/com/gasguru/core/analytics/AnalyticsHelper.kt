package com.gasguru.core.analytics

import kotlin.experimental.ExperimentalObjCName
import kotlin.native.ObjCName

@OptIn(ExperimentalObjCName::class)
@ObjCName("AnalyticsHelper", exact = true)
interface AnalyticsHelper {
    fun logEvent(event: AnalyticsEvent)
    fun updateSuperProperties(properties: Map<String, Any>)
}
