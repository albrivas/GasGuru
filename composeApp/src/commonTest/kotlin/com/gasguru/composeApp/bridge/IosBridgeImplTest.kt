package com.gasguru.composeApp.bridge

import com.gasguru.core.analytics.AnalyticsEvent
import com.gasguru.core.analytics.AnalyticsHelper
import com.gasguru.core.data.repository.stations.FuelStationRepository
import com.gasguru.core.domain.fuelstation.GetFuelStationUseCase
import com.gasguru.core.model.data.FuelStation
import com.gasguru.core.model.data.LatLng
import com.gasguru.core.model.data.OpeningHours
import com.gasguru.navigation.deeplink.DeepLinkStateHolder
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

@OptIn(ExperimentalCoroutinesApi::class)
class IosBridgeImplTest {

    @Test
    fun handlePushTap_setsPendingStationId() {
        val holder = DeepLinkStateHolder()
        val bridge = buildBridge(holder = holder)

        bridge.handlePushTap(stationId = 42)

        assertEquals(42, holder.pendingStationId.value)
    }

    @Test
    fun refreshStations_whenRepositorySucceeds_callsOnCompleteWithTrue() = runTest {
        val sharedDispatcher = StandardTestDispatcher(testScheduler)
        Dispatchers.setMain(sharedDispatcher)
        val scope = TestScope(sharedDispatcher)
        val bridge = buildBridge(
            repository = SucceedingFuelStationRepository(),
            scope = scope,
        )

        var result: Boolean? = null
        bridge.refreshStations { success -> result = success }
        advanceUntilIdle()

        Dispatchers.resetMain()
        assertTrue(result == true)
    }

    @Test
    fun refreshStations_whenRepositoryThrows_callsOnCompleteWithFalse() = runTest {
        val sharedDispatcher = StandardTestDispatcher(testScheduler)
        Dispatchers.setMain(sharedDispatcher)
        val scope = TestScope(sharedDispatcher)
        val analyticsHelper = RecordingAnalyticsHelper()
        val bridge = buildBridge(
            repository = FailingFuelStationRepository(),
            analyticsHelper = analyticsHelper,
            scope = scope,
        )

        var result: Boolean? = null
        bridge.refreshStations { success -> result = success }
        advanceUntilIdle()

        Dispatchers.resetMain()
        assertFalse(result == true)
        assertTrue(analyticsHelper.hasEvent(AnalyticsEvent.Types.STATION_SYNC_WORKER_RETRIED))
    }

    // --- helpers ---

    private fun buildBridge(
        holder: DeepLinkStateHolder = DeepLinkStateHolder(),
        analyticsHelper: AnalyticsHelper = RecordingAnalyticsHelper(),
        repository: FuelStationRepository = SucceedingFuelStationRepository(),
        scope: kotlinx.coroutines.CoroutineScope = TestScope(),
    ): IosBridgeImpl = IosBridgeImpl(
        deepLinkStateHolder = holder,
        analyticsHelper = analyticsHelper,
        getFuelStationUseCase = GetFuelStationUseCase(repository = repository),
        scope = scope,
    )
}

private class RecordingAnalyticsHelper : AnalyticsHelper {
    private val loggedEvents = mutableListOf<AnalyticsEvent>()

    override fun logEvent(event: AnalyticsEvent) {
        loggedEvents.add(event)
    }

    override fun updateSuperProperties(properties: Map<String, Any>) = Unit

    fun hasEvent(type: String): Boolean = loggedEvents.any { it.type == type }
}

private class SucceedingFuelStationRepository : FuelStationRepository {
    override suspend fun addAllStations() = Unit
    override fun getFuelStationByLocation(
        userLocation: LatLng,
        maxStations: Int,
        brands: List<String>,
        schedule: OpeningHours,
    ): Flow<List<FuelStation>> = flowOf(emptyList())
    override fun getFuelStationById(id: Int, userLocation: LatLng): Flow<FuelStation> =
        throw UnsupportedOperationException()
    override suspend fun getFuelStationInRoute(
        origin: LatLng,
        points: List<LatLng>,
    ): List<FuelStation> = emptyList()
}

private class FailingFuelStationRepository : FuelStationRepository {
    override suspend fun addAllStations() = throw RuntimeException("Network error")
    override fun getFuelStationByLocation(
        userLocation: LatLng,
        maxStations: Int,
        brands: List<String>,
        schedule: OpeningHours,
    ): Flow<List<FuelStation>> = flowOf(emptyList())
    override fun getFuelStationById(id: Int, userLocation: LatLng): Flow<FuelStation> =
        throw UnsupportedOperationException()
    override suspend fun getFuelStationInRoute(
        origin: LatLng,
        points: List<LatLng>,
    ): List<FuelStation> = emptyList()
}
