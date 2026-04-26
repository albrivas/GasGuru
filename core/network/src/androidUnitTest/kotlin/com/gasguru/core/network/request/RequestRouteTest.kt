package com.gasguru.core.network.request

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test

@DisplayName("RequestRoute data classes")
class RequestRouteTest {

    @Test
    @DisplayName(
        """
        GIVEN a fully built RequestRoute
        WHEN accessing all properties
        THEN values match what was provided
        """,
    )
    fun `RequestRoute properties are accessible`() {
        val latLng = RequestLatLng(latitude = 40.4168, longitude = -3.7038)
        val location = RequestLocation(latLng = latLng)
        val destination = RequestDestination(location = location)
        val origin = RequestOrigin(location = location)
        val requestRoute = RequestRoute(
            destination = destination,
            origin = origin,
            travelMode = "DRIVE",
            languageCode = "es",
            computeAlternativeRoutes = "false",
        )

        assertEquals(40.4168, requestRoute.destination.location.latLng.latitude)
        assertEquals(-3.7038, requestRoute.destination.location.latLng.longitude)
        assertEquals(40.4168, requestRoute.origin.location.latLng.latitude)
        assertEquals(-3.7038, requestRoute.origin.location.latLng.longitude)
        assertEquals("DRIVE", requestRoute.travelMode)
        assertEquals("es", requestRoute.languageCode)
        assertEquals("false", requestRoute.computeAlternativeRoutes)
    }
}
