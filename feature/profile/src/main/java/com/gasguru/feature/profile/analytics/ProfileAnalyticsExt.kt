package com.gasguru.feature.profile.analytics

import com.gasguru.core.analytics.AnalyticsEvent
import com.gasguru.core.analytics.AnalyticsHelper

fun AnalyticsHelper.trackVehicleDeleted(wasPrincipal: Boolean, vehiclesRemaining: Int) {
    logEvent(
        AnalyticsEvent(
            type = AnalyticsEvent.Types.VEHICLE_DELETED,
            extras = listOf(
                AnalyticsEvent.Param(key = AnalyticsEvent.ParamKeys.WAS_PRINCIPAL, value = wasPrincipal.toString()),
                AnalyticsEvent.Param(
                    key = AnalyticsEvent.ParamKeys.VEHICLES_REMAINING,
                    value = vehiclesRemaining.toString()
                ),
            ),
        )
    )
}
