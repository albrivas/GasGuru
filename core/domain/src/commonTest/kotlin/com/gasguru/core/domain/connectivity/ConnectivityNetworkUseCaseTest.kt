package com.gasguru.core.domain.connectivity

import app.cash.turbine.test
import com.gasguru.core.domain.fakes.FakeNetworkMonitor
import kotlinx.coroutines.test.runTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class ConnectivityNetworkUseCaseTest {

    private lateinit var sut: ConnectivityNetworkUseCase
    private lateinit var fakeNetworkMonitor: FakeNetworkMonitor

    @BeforeTest
    fun setUp() {
        fakeNetworkMonitor = FakeNetworkMonitor(initialOnline = true)
        sut = ConnectivityNetworkUseCase(networkMonitor = fakeNetworkMonitor)
    }

    @Test
    fun emitsTrueWhenOnline() = runTest {
        sut().test {
            assertTrue(awaitItem())
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun emitsFalseWhenOffline() = runTest {
        val offlineMonitor = FakeNetworkMonitor(initialOnline = false)
        val offlineSut = ConnectivityNetworkUseCase(networkMonitor = offlineMonitor)

        offlineSut().test {
            assertFalse(awaitItem())
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun emitsUpdatesWhenConnectivityChanges() = runTest {
        sut().test {
            assertTrue(awaitItem())

            fakeNetworkMonitor.setOnline(false)
            assertFalse(awaitItem())

            fakeNetworkMonitor.setOnline(true)
            assertTrue(awaitItem())

            cancelAndIgnoreRemainingEvents()
        }
    }
}
