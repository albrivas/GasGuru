package com.gasguru.core.data.mapper

import com.gasguru.core.model.data.LatLng
import com.gasguru.core.model.data.Route
import com.gasguru.core.network.model.route.NetworkRoutes
import com.google.maps.android.PolyUtil

fun NetworkRoutes.toDomainRoute(): Route {
    val firstRoute = routes.first()

    val route = routes.flatMap { route ->
        PolyUtil.decode(route.polyline.encodedPolyline).map { point: com.google.android.gms.maps.model.LatLng ->
            LatLng(
                latitude = point.latitude,
                longitude = point.longitude,
            )
        }
    }

    val distanceText = firstRoute.localizedValues.distance.text
    val durationText = firstRoute.localizedValues.duration?.text
        ?: firstRoute.localizedValues.staticDuration.text

    return Route(
        route = route,
        distanceText = distanceText,
        durationText = durationText,
    )
}
