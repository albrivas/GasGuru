package com.gasguru.feature.vehicle.analytics

import com.gasguru.core.analytics.AnalyticsEvent
import com.gasguru.core.analytics.AnalyticsHelper

fun AnalyticsHelper.trackVehicleCreated(
    vehicleType: String,
    fuelType: String,
    capacityLitres: Int,
    isPrincipal: Boolean,
) {
    logEvent(
        AnalyticsEvent(
            type = AnalyticsEvent.Types.VEHICLE_CREATED,
            extras = listOf(
                AnalyticsEvent.Param(key = AnalyticsEvent.ParamKeys.VEHICLE_TYPE, value = vehicleType),
                AnalyticsEvent.Param(key = AnalyticsEvent.ParamKeys.FUEL_TYPE, value = fuelType),
                AnalyticsEvent.Param(key = AnalyticsEvent.ParamKeys.CAPACITY_LITRES, value = capacityLitres.toString()),
                AnalyticsEvent.Param(key = AnalyticsEvent.ParamKeys.IS_PRINCIPAL, value = isPrincipal.toString()),
            ),
        )
    )
}

fun AnalyticsHelper.trackVehicleEdited(vehicleType: String, fuelType: String) {
    logEvent(
        AnalyticsEvent(
            type = AnalyticsEvent.Types.VEHICLE_EDITED,
            extras = listOf(
                AnalyticsEvent.Param(key = AnalyticsEvent.ParamKeys.VEHICLE_TYPE, value = vehicleType),
                AnalyticsEvent.Param(key = AnalyticsEvent.ParamKeys.FUEL_TYPE, value = fuelType),
            ),
        )
    )
}
