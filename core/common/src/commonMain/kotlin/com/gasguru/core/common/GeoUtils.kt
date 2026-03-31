package com.gasguru.core.common

import com.gasguru.core.model.data.LatLng
import kotlin.math.PI
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.sin
import kotlin.math.sqrt

private const val EARTH_RADIUS_METERS = 6371000.0

fun LatLng.distanceTo(other: LatLng): Float {
    val lat1 = latitude * PI / 180.0
    val lat2 = other.latitude * PI / 180.0
    val deltaLat = (other.latitude - latitude) * PI / 180.0
    val deltaLng = (other.longitude - longitude) * PI / 180.0

    val a = sin(deltaLat / 2) * sin(deltaLat / 2) +
        cos(lat1) * cos(lat2) *
        sin(deltaLng / 2) * sin(deltaLng / 2)
    val c = 2 * atan2(sqrt(a), sqrt(1 - a))
    return (EARTH_RADIUS_METERS * c).toFloat()
}
