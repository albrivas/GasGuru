package com.gasguru.core.data.repository.maps

import com.gasguru.core.model.data.LatLng

interface StaticMapRepository {
    fun generateStaticMapUrl(
        location: LatLng,
        zoom: Int,
        width: Int,
        height: Int,
    ): String
}
