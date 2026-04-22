package com.gasguru.core.domain.location

import app.cash.turbine.test
import com.gasguru.core.domain.fakes.FakeLocationTracker
import com.gasguru.core.model.data.LatLng
import kotlinx.coroutines.test.runTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull

class GetLastKnownLocationUseCaseTest {

    private lateinit var sut: GetLastKnownLocationUseCase
    private lateinit var fakeLocationTracker: FakeLocationTracker

    @BeforeTest
    fun setUp() {
        fakeLocationTracker = FakeLocationTracker()
        sut = GetLastKnownLocationUseCase(locationTracker = fakeLocationTracker)
    }

    @Test
    fun emitsNullWhenNoLastLocationKnown() = runTest {
        sut().test {
            assertNull(awaitItem())
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun emitsLastKnownLocationWhenSet() = runTest {
        val location = LatLng(latitude = 40.4, longitude = -3.7)
        val tracker = FakeLocationTracker(lastKnownLocation = location)
        val sutWithLocation = GetLastKnownLocationUseCase(locationTracker = tracker)

        sutWithLocation().test {
            assertEquals(location, awaitItem())
            cancelAndIgnoreRemainingEvents()
        }
    }
}
