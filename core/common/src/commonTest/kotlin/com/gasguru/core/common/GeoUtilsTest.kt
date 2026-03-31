package com.gasguru.core.common

import com.gasguru.core.model.data.LatLng
import io.kotest.matchers.floats.shouldBeWithinPercentageOf
import io.kotest.matchers.shouldBe
import kotlin.test.Test

class GeoUtilsTest {

    @Test
    fun `GIVEN same coordinates WHEN distanceTo THEN returns zero`() {
        val location = LatLng(latitude = 40.4168, longitude = -3.7038)
        location.distanceTo(other = location) shouldBe 0f
    }

    @Test
    fun `GIVEN Madrid and Sevilla WHEN distanceTo THEN returns approximately 391km`() {
        val madrid = LatLng(latitude = 40.4168, longitude = -3.7038)
        val sevilla = LatLng(latitude = 37.3886, longitude = -5.9823)
        val distanceMeters = madrid.distanceTo(other = sevilla)
        distanceMeters.shouldBeWithinPercentageOf(391_000f, percentage = 1.0)
    }

    @Test
    fun `GIVEN two points WHEN distanceTo THEN is symmetric`() {
        val pointA = LatLng(latitude = 41.3851, longitude = 2.1734)
        val pointB = LatLng(latitude = 39.4699, longitude = -0.3763)
        val distanceAtoB = pointA.distanceTo(other = pointB)
        val distanceBtoA = pointB.distanceTo(other = pointA)
        distanceAtoB.shouldBeWithinPercentageOf(distanceBtoA, percentage = 0.001)
    }

    @Test
    fun `GIVEN points on same latitude WHEN distanceTo THEN distance grows with longitude difference`() {
        val origin = LatLng(latitude = 40.0, longitude = 0.0)
        val close = LatLng(latitude = 40.0, longitude = 1.0)
        val far = LatLng(latitude = 40.0, longitude = 2.0)
        val distanceClose = origin.distanceTo(other = close)
        val distanceFar = origin.distanceTo(other = far)
        assert(distanceFar > distanceClose)
    }
}
