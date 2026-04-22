package com.gasguru.core.domain.location

import app.cash.turbine.test
import com.gasguru.core.domain.fakes.FakeLocationTracker
import com.gasguru.core.model.data.LatLng
import kotlinx.coroutines.test.runTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull

class GetCurrentLocationFlowUseCaseTest {

    private lateinit var sut: GetCurrentLocationFlowUseCase
    private lateinit var fakeLocationTracker: FakeLocationTracker

    @BeforeTest
    fun setUp() {
        fakeLocationTracker = FakeLocationTracker()
        sut = GetCurrentLocationFlowUseCase(locationTracker = fakeLocationTracker)
    }

    @Test
    fun emitsNullWhenNoLocationAvailable() = runTest {
        sut().test {
            assertNull(awaitItem())
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun emitsLocationWhenAvailable() = runTest {
        val location = LatLng(latitude = 40.4, longitude = -3.7)
        fakeLocationTracker.setLastKnownLocation(location)

        sut().test {
            assertEquals(location, awaitItem())
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun emitsUpdatesWhenLocationChanges() = runTest {
        val firstLocation = LatLng(latitude = 40.4, longitude = -3.7)
        val secondLocation = LatLng(latitude = 41.0, longitude = -4.0)

        sut().test {
            assertNull(awaitItem())

            fakeLocationTracker.setLastKnownLocation(firstLocation)
            assertEquals(firstLocation, awaitItem())

            fakeLocationTracker.setLastKnownLocation(secondLocation)
            assertEquals(secondLocation, awaitItem())

            cancelAndIgnoreRemainingEvents()
        }
    }
}
