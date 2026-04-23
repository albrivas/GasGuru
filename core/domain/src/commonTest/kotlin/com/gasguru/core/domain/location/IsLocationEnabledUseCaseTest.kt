package com.gasguru.core.domain.location

import app.cash.turbine.test
import com.gasguru.core.domain.fakes.FakeLocationTracker
import kotlinx.coroutines.test.runTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class IsLocationEnabledUseCaseTest {

    private lateinit var sut: IsLocationEnabledUseCase
    private lateinit var fakeLocationTracker: FakeLocationTracker

    @BeforeTest
    fun setUp() {
        fakeLocationTracker = FakeLocationTracker(isLocationEnabled = true)
        sut = IsLocationEnabledUseCase(locationTracker = fakeLocationTracker)
    }

    @Test
    fun emitsTrueWhenLocationEnabled() = runTest {
        sut().test {
            assertTrue(awaitItem())
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun emitsFalseWhenLocationDisabled() = runTest {
        val disabledTracker = FakeLocationTracker(isLocationEnabled = false)
        val sutDisabled = IsLocationEnabledUseCase(locationTracker = disabledTracker)

        sutDisabled().test {
            assertFalse(awaitItem())
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun emitsUpdatesWhenLocationEnabledStateChanges() = runTest {
        sut().test {
            assertTrue(awaitItem())

            fakeLocationTracker.setLocationEnabled(false)
            assertFalse(awaitItem())

            fakeLocationTracker.setLocationEnabled(true)
            assertTrue(awaitItem())

            cancelAndIgnoreRemainingEvents()
        }
    }
}
