package com.gasguru.core.data.repository.maps

import android.location.Location

interface StaticMapRepository {
    fun generateStaticMapUrl(
        location: Location,
        zoom: Int,
        width: Int,
        height: Int,
    ): String
}
