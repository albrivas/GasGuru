package com.gasguru.core.model.data

data class MapBounds(
    val southwest: LatLng,
    val northeast: LatLng,
) {
    val center: LatLng
        get() = LatLng(
            latitude = (southwest.latitude + northeast.latitude) / 2.0,
            longitude = (southwest.longitude + northeast.longitude) / 2.0,
        )
}
