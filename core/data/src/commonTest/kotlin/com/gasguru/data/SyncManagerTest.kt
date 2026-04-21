package com.gasguru.data

import com.gasguru.core.data.sync.SyncManager
import com.gasguru.data.fakes.FakeNetworkMonitor
import com.gasguru.data.fakes.FakePriceAlertRepository
import kotlin.test.Test
import kotlin.test.assertFalse
import kotlin.test.assertTrue
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.runTest

@OptIn(ExperimentalCoroutinesApi::class)
class SyncManagerTest {

    @Test
    fun execute_whenOnlineAndHasPendingSync_callsSync() = runTest(UnconfinedTestDispatcher()) {
        val fakePriceAlertRepository = FakePriceAlertRepository(pendingSync = true)
        SyncManager(
            networkMonitor = FakeNetworkMonitor(initialOnline = true),
            priceAlertRepository = fakePriceAlertRepository,
            scope = backgroundScope,
        ).execute()

        assertTrue(fakePriceAlertRepository.syncCalled)
    }

    @Test
    fun execute_whenOnlineButNoPendingSync_doesNotCallSync() = runTest(UnconfinedTestDispatcher()) {
        val fakePriceAlertRepository = FakePriceAlertRepository(pendingSync = false)
        SyncManager(
            networkMonitor = FakeNetworkMonitor(initialOnline = true),
            priceAlertRepository = fakePriceAlertRepository,
            scope = backgroundScope,
        ).execute()

        assertFalse(fakePriceAlertRepository.syncCalled)
    }

    @Test
    fun execute_whenOfflineWithPendingSync_doesNotCallSync() = runTest(UnconfinedTestDispatcher()) {
        val fakePriceAlertRepository = FakePriceAlertRepository(pendingSync = true)
        SyncManager(
            networkMonitor = FakeNetworkMonitor(initialOnline = false),
            priceAlertRepository = fakePriceAlertRepository,
            scope = backgroundScope,
        ).execute()

        assertFalse(fakePriceAlertRepository.syncCalled)
    }

    @Test
    fun execute_whenGoesOnlineAfterOffline_callsSyncOnConnectivity() = runTest(UnconfinedTestDispatcher()) {
        val fakeNetworkMonitor = FakeNetworkMonitor(initialOnline = false)
        val fakePriceAlertRepository = FakePriceAlertRepository(pendingSync = true)
        SyncManager(
            networkMonitor = fakeNetworkMonitor,
            priceAlertRepository = fakePriceAlertRepository,
            scope = backgroundScope,
        ).execute()

        assertFalse(fakePriceAlertRepository.syncCalled)

        fakeNetworkMonitor.setOnline(online = true)

        assertTrue(fakePriceAlertRepository.syncCalled)
    }
}
