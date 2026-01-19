package com.gasguru.core.testing.fakes.data.maps

import com.gasguru.core.data.repository.maps.StaticMapRepository
import com.gasguru.core.model.data.LatLng

class FakeStaticMapRepository : StaticMapRepository {
    var lastLocation: LatLng? = null
        private set
    var lastZoom: Int? = null
        private set
    var lastWidth: Int? = null
        private set
    var lastHeight: Int? = null
        private set

    var urlToReturn: String = "static://map"

    override fun generateStaticMapUrl(
        location: LatLng,
        zoom: Int,
        width: Int,
        height: Int,
    ): String {
        lastLocation = location
        lastZoom = zoom
        lastWidth = width
        lastHeight = height
        return urlToReturn
    }
}
