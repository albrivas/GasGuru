package com.gasguru.feature.station_map.platform

import com.gasguru.core.model.data.LatLng
import com.gasguru.feature.station_map.ui.model.GeoBounds
import com.google.android.gms.maps.model.LatLngBounds
import com.google.android.gms.maps.model.LatLng as GoogleLatLng

internal fun GeoBounds.toGoogleLatLngBounds(): LatLngBounds =
    LatLngBounds.Builder()
        .include(GoogleLatLng(southwest.latitude, southwest.longitude))
        .include(GoogleLatLng(northeast.latitude, northeast.longitude))
        .build()

internal fun LatLngBounds.toGeoBounds(): GeoBounds =
    GeoBounds(
        southwest = LatLng(latitude = southwest.latitude, longitude = southwest.longitude),
        northeast = LatLng(latitude = northeast.latitude, longitude = northeast.longitude),
    )

internal fun LatLng.toGoogleLatLng(): GoogleLatLng = GoogleLatLng(latitude, longitude)
