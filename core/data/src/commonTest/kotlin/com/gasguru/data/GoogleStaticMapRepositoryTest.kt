package com.gasguru.data

import com.gasguru.core.data.repository.maps.GoogleStaticMapRepository
import com.gasguru.core.model.data.LatLng
import kotlin.test.Test
import kotlin.test.assertTrue

class GoogleStaticMapRepositoryTest {

    private val apiKey = "test-api-key"
    private val sut = GoogleStaticMapRepository(apiKey = apiKey)

    @Test
    fun generateStaticMapUrl_withValidLocation_containsCorrectCenterParameter() {
        val location = LatLng(latitude = 40.416775, longitude = -3.70379)

        val url = sut.generateStaticMapUrl(location = location, zoom = 15, width = 400, height = 300)

        assertTrue(url.contains("center=40.416775,-3.70379"))
    }

    @Test
    fun generateStaticMapUrl_withValidLocation_containsZoomAndSizeParameters() {
        val location = LatLng(latitude = 40.0, longitude = -3.0)

        val url = sut.generateStaticMapUrl(location = location, zoom = 12, width = 800, height = 600)

        assertTrue(url.contains("zoom=12"))
        assertTrue(url.contains("size=800x600"))
    }

    @Test
    fun generateStaticMapUrl_withValidLocation_containsApiKey() {
        val location = LatLng(latitude = 40.0, longitude = -3.0)

        val url = sut.generateStaticMapUrl(location = location, zoom = 10, width = 400, height = 300)

        assertTrue(url.contains("key=test-api-key"))
    }

    @Test
    fun generateStaticMapUrl_withValidLocation_containsRedMarker() {
        val location = LatLng(latitude = 40.0, longitude = -3.0)

        val url = sut.generateStaticMapUrl(location = location, zoom = 10, width = 400, height = 300)

        assertTrue(url.contains("markers="))
    }

    @Test
    fun generateStaticMapUrl_withValidLocation_startsWithGoogleMapsBaseUrl() {
        val location = LatLng(latitude = 40.0, longitude = -3.0)

        val url = sut.generateStaticMapUrl(location = location, zoom = 10, width = 400, height = 300)

        assertTrue(url.startsWith("https://maps.googleapis.com/maps/api/staticmap"))
    }
}
