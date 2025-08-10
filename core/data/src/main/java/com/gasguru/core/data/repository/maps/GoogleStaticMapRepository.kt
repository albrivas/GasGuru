package com.gasguru.core.data.repository.maps

import android.location.Location
import javax.inject.Inject
import javax.inject.Named

class GoogleStaticMapRepository @Inject constructor(
    @Named("google_api_key") private val apiKey: String,
) : StaticMapRepository {

    override fun generateStaticMapUrl(
        location: Location,
        zoom: Int,
        width: Int,
        height: Int,
    ): String {
        val center = "${location.latitude},${location.longitude}"
        return "https://maps.googleapis.com/maps/api/staticmap?" +
            "center=$center" +
            "&zoom=$zoom" +
            "&size=${width}x$height" +
            "&markers=color:red%7Clabel:C%7C$center" +
            "&key=$apiKey"
    }
}