package com.gasguru.feature.station_map.ui.model

import com.gasguru.core.model.data.LatLng

data class GeoBounds(
    val southwest: LatLng,
    val northeast: LatLng,
) {
    companion object {
        fun fromPoints(points: List<LatLng>): GeoBounds? {
            if (points.isEmpty()) return null
            val minLat = points.minOf { it.latitude }
            val maxLat = points.maxOf { it.latitude }
            val minLng = points.minOf { it.longitude }
            val maxLng = points.maxOf { it.longitude }
            return GeoBounds(
                southwest = LatLng(latitude = minLat, longitude = minLng),
                northeast = LatLng(latitude = maxLat, longitude = maxLng),
            )
        }
    }
}
