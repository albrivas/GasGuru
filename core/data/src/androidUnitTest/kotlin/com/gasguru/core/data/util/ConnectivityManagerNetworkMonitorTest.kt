package com.gasguru.core.data.util

import android.content.Context
import android.net.ConnectivityManager
import android.net.ConnectivityManager.NetworkCallback
import android.net.Network
import android.net.NetworkCapabilities
import app.cash.turbine.test
import com.gasguru.core.testing.CoroutinesTestExtension
import io.mockk.Runs
import io.mockk.every
import io.mockk.junit5.MockKExtension
import io.mockk.just
import io.mockk.mockk
import io.mockk.mockkConstructor
import io.mockk.slot
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import kotlin.test.assertFalse
import kotlin.test.assertTrue

@OptIn(ExperimentalCoroutinesApi::class)
@ExtendWith(MockKExtension::class)
@ExtendWith(CoroutinesTestExtension::class)
@DisplayName("ConnectivityManagerNetworkMonitor")
class ConnectivityManagerNetworkMonitorTest {

    private lateinit var context: Context
    private lateinit var connectivityManager: ConnectivityManager
    private lateinit var sut: ConnectivityManagerNetworkMonitor
    private val callbackSlot = slot<NetworkCallback>()

    @BeforeEach
    fun setUp() {
        mockkConstructor(android.net.NetworkRequest.Builder::class)
        every { anyConstructed<android.net.NetworkRequest.Builder>().addCapability(any()) } returns mockk(relaxed = true)
        every { anyConstructed<android.net.NetworkRequest.Builder>().build() } returns mockk(relaxed = true)

        context = mockk()
        connectivityManager = mockk(relaxed = true)
        every { context.getSystemService(ConnectivityManager::class.java) } returns connectivityManager
        every { context.getSystemService(any<String>()) } returns connectivityManager
        every { connectivityManager.registerNetworkCallback(any(), capture(callbackSlot)) } just Runs
        sut = ConnectivityManagerNetworkMonitor(
            context = context,
            ioDispatcher = kotlinx.coroutines.Dispatchers.Unconfined,
        )
    }

    @Test
    @DisplayName(
        """
        GIVEN active network with internet capability
        WHEN flow is collected
        THEN emits true initially
        """
    )
    fun emitsTrueWhenInitiallyConnected() = runTest {
        val network = mockk<Network>()
        val capabilities = mockk<NetworkCapabilities>()
        every { connectivityManager.activeNetwork } returns network
        every { connectivityManager.getNetworkCapabilities(network) } returns capabilities
        every { capabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET) } returns true

        sut.isOnline.test {
            assertTrue(awaitItem())
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    @DisplayName(
        """
        GIVEN no active network
        WHEN flow is collected
        THEN emits false initially
        """
    )
    fun emitsFalseWhenInitiallyDisconnected() = runTest {
        every { connectivityManager.activeNetwork } returns null

        sut.isOnline.test {
            assertFalse(awaitItem())
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    @DisplayName(
        """
        GIVEN offline device
        WHEN network becomes available
        THEN emits true
        """
    )
    fun emitsTrueWhenNetworkBecomesAvailable() = runTest {
        every { connectivityManager.activeNetwork } returns null
        val network = mockk<Network>()

        sut.isOnline.test {
            assertFalse(awaitItem())
            callbackSlot.captured.onAvailable(network)
            assertTrue(awaitItem())
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    @DisplayName(
        """
        GIVEN online device
        WHEN network is lost
        THEN emits false
        """
    )
    fun emitsFalseWhenNetworkIsLost() = runTest {
        val network = mockk<Network>()
        val capabilities = mockk<NetworkCapabilities>()
        every { connectivityManager.activeNetwork } returns network
        every { connectivityManager.getNetworkCapabilities(network) } returns capabilities
        every { capabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET) } returns true

        sut.isOnline.test {
            assertTrue(awaitItem())
            callbackSlot.captured.onAvailable(network)
            callbackSlot.captured.onLost(network)
            assertFalse(awaitItem())
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    @DisplayName(
        """
        GIVEN two active networks
        WHEN one network is lost
        THEN remains online
        """
    )
    fun remainsTrueWhenOneOfTwoNetworksIsLost() = runTest {
        every { connectivityManager.activeNetwork } returns null
        val network1 = mockk<Network>()
        val network2 = mockk<Network>()

        sut.isOnline.test {
            assertFalse(awaitItem())
            callbackSlot.captured.onAvailable(network1)
            assertTrue(awaitItem())
            callbackSlot.captured.onAvailable(network2)
            callbackSlot.captured.onLost(network1)
            // still online — distinctUntilChanged swallows the duplicate true
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    @DisplayName(
        """
        GIVEN ConnectivityManager is null
        WHEN flow is collected
        THEN emits false and completes
        """
    )
    fun emitsFalseAndCompletesWhenConnectivityManagerIsNull() = runTest {
        every { context.getSystemService(ConnectivityManager::class.java) } returns null
        every { context.getSystemService(any<String>()) } returns null
        val monitor = ConnectivityManagerNetworkMonitor(
            context = context,
            ioDispatcher = kotlinx.coroutines.Dispatchers.Unconfined,
        )

        monitor.isOnline.test {
            assertFalse(awaitItem())
            awaitComplete()
        }
    }
}
