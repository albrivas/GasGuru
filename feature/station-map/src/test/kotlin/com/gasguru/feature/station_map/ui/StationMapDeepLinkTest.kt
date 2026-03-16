package com.gasguru.feature.station_map.ui

import com.gasguru.core.testing.CoroutinesTestExtension
import com.gasguru.core.testing.fakes.navigation.FakeNavigationManager
import com.gasguru.navigation.deeplink.DeepLinkStateHolder
import com.gasguru.navigation.manager.NavigationDestination
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith

@OptIn(ExperimentalCoroutinesApi::class)
@ExtendWith(CoroutinesTestExtension::class)
class StationMapDeepLinkTest {

    private lateinit var deepLinkStateHolder: DeepLinkStateHolder
    private lateinit var fakeNavigationManager: FakeNavigationManager

    @BeforeEach
    fun setUp() {
        deepLinkStateHolder = DeepLinkStateHolder()
        fakeNavigationManager = FakeNavigationManager()
    }

    @Test
    @DisplayName(
        """
        GIVEN pendingStationId is set
        WHEN deep link is consumed (clear + navigateTo)
        THEN pendingStationId is null and navigateTo was called exactly once
    """
    )
    fun consumeDeepLinkNavigatesOnce() = runTest {
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
    @DisplayName(
        """
        GIVEN pendingStationId was consumed (null after clear)
        WHEN setPendingStationId is called again with the same value
        THEN StateFlow emits the new value (null → id transition triggers new navigation)
    """
    )
    fun pendingStationIdEmitsAfterClearAndReSet() = runTest {
        val emittedValues = mutableListOf<Int?>()
        val job = launch {
            deepLinkStateHolder.pendingStationId.collect { value -> emittedValues.add(value) }
        }

        deepLinkStateHolder.setPendingStationId(stationId = 123)
        deepLinkStateHolder.clear()
        deepLinkStateHolder.setPendingStationId(stationId = 123)

        advanceUntilIdle()
        job.cancel()

        // null (initial) → 123 (first set) → null (clear) → 123 (re-set)
        assertEquals(listOf(null, 123, null, 123), emittedValues)
    }

    @Test
    @DisplayName(
        """
        GIVEN pendingStationId is set to a value
        WHEN setPendingStationId is called again with the same value (no clear in between)
        THEN StateFlow does NOT emit a duplicate (StateFlow deduplicates equal values)
    """
    )
    fun setPendingStationIdWithSameValueIsDeduplicatedByStateFlow() = runTest {
        val emittedValues = mutableListOf<Int?>()
        val job = launch {
            deepLinkStateHolder.pendingStationId.collect { value -> emittedValues.add(value) }
        }

        deepLinkStateHolder.setPendingStationId(stationId = 123)
        deepLinkStateHolder.setPendingStationId(stationId = 123)

        advanceUntilIdle()
        job.cancel()

        // null (initial) → 123 (first set only — second set deduplicated)
        assertEquals(listOf(null, 123), emittedValues)
    }
}
