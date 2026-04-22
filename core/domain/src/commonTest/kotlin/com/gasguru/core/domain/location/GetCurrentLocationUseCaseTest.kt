package com.gasguru.core.domain.location

import com.gasguru.core.domain.fakes.FakeLocationTracker
import com.gasguru.core.model.data.LatLng
import kotlinx.coroutines.test.runTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull

class GetCurrentLocationUseCaseTest {

    private lateinit var sut: GetCurrentLocationUseCase
    private lateinit var fakeLocationTracker: FakeLocationTracker

    @BeforeTest
    fun setUp() {
        fakeLocationTracker = FakeLocationTracker()
        sut = GetCurrentLocationUseCase(locationTracker = fakeLocationTracker)
    }

    @Test
    fun returnsNullWhenNoLocationAvailable() = runTest {
        val result = sut()

        assertNull(result)
    }

    @Test
    fun returnsCurrentLocationWhenAvailable() = runTest {
        val location = LatLng(latitude = 40.4, longitude = -3.7)
        fakeLocationTracker.setLastKnownLocation(location)

        val result = sut()

        assertEquals(location, result)
    }
}
