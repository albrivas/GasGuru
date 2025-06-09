package com.gasguru.core.data.mapper

import android.location.Location
import com.gasguru.core.model.data.Route
import com.gasguru.core.network.model.route.NetworkRoutes

fun NetworkRoutes.toDomainRoute(): Route {
    val legs = routes.map { it.polyline.encodedPolyline }

    val steps = routes.flatMap { route ->
        route.legs.flatMap { leg ->
            leg.steps.map { step ->
                Location("").apply {
                    latitude = step.startLocation.latLng.latitude
                    longitude = step.startLocation.latLng.longitude
                }
            }
        }
    }

    return Route(legs = legs, steps = steps)
}
