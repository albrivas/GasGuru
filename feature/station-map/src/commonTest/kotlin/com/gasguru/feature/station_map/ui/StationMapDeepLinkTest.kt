package com.gasguru.feature.station_map.ui

import com.gasguru.core.testing.CoroutineTest
import com.gasguru.core.testing.fakes.navigation.FakeNavigationManager
import com.gasguru.navigation.deeplink.DeepLinkStateHolder
import com.gasguru.navigation.manager.NavigationDestination
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.advanceUntilIdle
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull

@OptIn(ExperimentalCoroutinesApi::class)
class StationMapDeepLinkTest : CoroutineTest() {

    private lateinit var deepLinkStateHolder: DeepLinkStateHolder
    private lateinit var fakeNavigationManager: FakeNavigationManager

    @BeforeTest
    fun setUp() {
        deepLinkStateHolder = DeepLinkStateHolder()
        fakeNavigationManager = FakeNavigationManager()
    }

    @Test
    fun `GIVEN pending station id in deep link holder WHEN deep link is consumed and navigation is triggered THEN only one destination is navigated and pending id is cleared`() = runTest {
        deepLinkStateHolder.setPendingStationId(stationId = 123)

        val stationId = deepLinkStateHolder.pendingStationId.value!!
        deepLinkStateHolder.clear()
        fakeNavigationManager.navigateTo(
            destination = NavigationDestination.DetailStation(
                idServiceStation = stationId,
                presentAsDialog = true,
            ),
        )

        assertNull(deepLinkStateHolder.pendingStationId.value)
        assertEquals(1, fakeNavigationManager.navigatedDestinations.size)
        assertEquals(
            NavigationDestination.DetailStation(idServiceStation = 123, presentAsDialog = true),
            fakeNavigationManager.navigatedDestinations.first(),
        )
    }

    @Test
    fun `GIVEN pending station id is set then cleared then set again WHEN collecting the flow THEN all state transitions are emitted in order`() = runTest {
        val emittedValues = mutableListOf<Int?>()
        val job = launch {
            deepLinkStateHolder.pendingStationId.collect { value -> emittedValues.add(value) }
        }
        advanceUntilIdle()

        deepLinkStateHolder.setPendingStationId(stationId = 123)
        advanceUntilIdle()
        deepLinkStateHolder.clear()
        advanceUntilIdle()
        deepLinkStateHolder.setPendingStationId(stationId = 123)
        advanceUntilIdle()
        job.cancel()

        // null (initial) → 123 (first set) → null (clear) → 123 (re-set)
        assertEquals(listOf(null, 123, null, 123), emittedValues)
    }

    @Test
    fun `GIVEN pending station id is set twice with the same value WHEN collecting the flow THEN the second emission is deduplicated by StateFlow`() = runTest {
        val emittedValues = mutableListOf<Int?>()
        val job = launch {
            deepLinkStateHolder.pendingStationId.collect { value -> emittedValues.add(value) }
        }
        advanceUntilIdle()

        deepLinkStateHolder.setPendingStationId(stationId = 123)
        deepLinkStateHolder.setPendingStationId(stationId = 123)

        advanceUntilIdle()
        job.cancel()

        // null (initial) → 123 (first set only — second set deduplicated)
        assertEquals(listOf(null, 123), emittedValues)
    }
}
