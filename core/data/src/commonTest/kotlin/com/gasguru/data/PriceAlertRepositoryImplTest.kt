package com.gasguru.data

import com.gasguru.core.analytics.AnalyticsEvent
import com.gasguru.core.data.repository.alerts.PriceAlertRepositoryImpl
import com.gasguru.core.database.model.ModificationType
import com.gasguru.core.database.model.PriceAlertEntity
import com.gasguru.core.database.model.VehicleEntity
import com.gasguru.core.model.data.FuelType
import com.gasguru.core.model.data.VehicleType
import com.gasguru.data.fakes.FakeAnalyticsHelper
import com.gasguru.data.fakes.FakeNetworkMonitor
import com.gasguru.data.fakes.FakeOneSignalManager
import com.gasguru.data.fakes.FakePriceAlertDao
import com.gasguru.data.fakes.FakeSupabaseManager
import com.gasguru.data.fakes.FakeVehicleDao
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue
import kotlinx.coroutines.test.runTest

class PriceAlertRepositoryImplTest {

    private lateinit var fakePriceAlertDao: FakePriceAlertDao
    private lateinit var fakeVehicleDao: FakeVehicleDao
    private lateinit var fakeSupabaseManager: FakeSupabaseManager
    private lateinit var fakeNetworkMonitor: FakeNetworkMonitor
    private lateinit var fakeOneSignalManager: FakeOneSignalManager
    private lateinit var fakeAnalyticsHelper: FakeAnalyticsHelper
    private lateinit var sut: PriceAlertRepositoryImpl

    private val vehicleEntity = VehicleEntity(
        id = 1L,
        userId = 0L,
        name = "Golf VII",
        fuelType = FuelType.GASOLINE_95,
        tankCapacity = 55,
        vehicleType = VehicleType.CAR,
        isPrincipal = true,
    )

    @BeforeTest
    fun setUp() {
        fakePriceAlertDao = FakePriceAlertDao()
        fakeVehicleDao = FakeVehicleDao(initialVehicles = listOf(vehicleEntity))
        fakeSupabaseManager = FakeSupabaseManager()
        fakeNetworkMonitor = FakeNetworkMonitor(initialOnline = true)
        fakeOneSignalManager = FakeOneSignalManager()
        fakeAnalyticsHelper = FakeAnalyticsHelper()
        sut = PriceAlertRepositoryImpl(
            priceAlertDao = fakePriceAlertDao,
            supabaseManager = fakeSupabaseManager,
            networkMonitor = fakeNetworkMonitor,
            oneSignalManager = fakeOneSignalManager,
            vehicleDao = fakeVehicleDao,
            analyticsHelper = fakeAnalyticsHelper,
        )
    }

    // region addPriceAlert

    @Test
    fun addPriceAlert_withNoExistingAlerts_enablesNotificationsAndInsertsLocally() = runTest {
        sut.addPriceAlert(stationId = 1, lastNotifiedPrice = 1.50)

        assertTrue(fakeOneSignalManager.enabledStates.contains(true))
        assertTrue(fakeSupabaseManager.addedAlerts.contains(1))
    }

    @Test
    fun addPriceAlert_withExistingAlerts_doesNotReEnableNotifications() = runTest {
        fakePriceAlertDao.insert(
            PriceAlertEntity(stationId = 99, lastNotifiedPrice = 1.40, isSynced = true),
        )

        sut.addPriceAlert(stationId = 1, lastNotifiedPrice = 1.50)

        assertFalse(fakeOneSignalManager.enabledStates.contains(true))
    }

    @Test
    fun addPriceAlert_whenOnline_syncesToSupabaseAndMarksAsSynced() = runTest {
        fakeNetworkMonitor.setOnline(online = true)

        sut.addPriceAlert(stationId = 1, lastNotifiedPrice = 1.50)

        assertTrue(fakeSupabaseManager.addedAlerts.contains(1))
        assertFalse(fakePriceAlertDao.hasPendingSync())
    }

    @Test
    fun addPriceAlert_whenOffline_insertsLocallyWithoutSyncing() = runTest {
        fakeNetworkMonitor.setOnline(online = false)

        sut.addPriceAlert(stationId = 1, lastNotifiedPrice = 1.50)

        assertTrue(fakeSupabaseManager.addedAlerts.isEmpty())
        assertTrue(fakePriceAlertDao.hasPendingSync())
    }

    // endregion

    // region removePriceAlert

    @Test
    fun removePriceAlert_whenAlertNotSynced_deletesLocallyOnly() = runTest {
        fakePriceAlertDao.insert(
            PriceAlertEntity(stationId = 1, lastNotifiedPrice = 1.50, isSynced = false),
        )

        sut.removePriceAlert(stationId = 1)

        assertFalse(fakePriceAlertDao.hasPendingSync())
        assertTrue(fakeSupabaseManager.removedAlerts.isEmpty())
    }

    @Test
    fun removePriceAlert_whenSyncedAndOnline_deletesLocallyAndFromSupabase() = runTest {
        fakeNetworkMonitor.setOnline(online = true)
        fakePriceAlertDao.insert(
            PriceAlertEntity(stationId = 1, lastNotifiedPrice = 1.50, isSynced = true),
        )

        sut.removePriceAlert(stationId = 1)

        assertTrue(fakeSupabaseManager.removedAlerts.contains(1))
        assertFalse(fakePriceAlertDao.hasPendingSync())
    }

    @Test
    fun removePriceAlert_whenSyncedAndOffline_marksAsDeletePending() = runTest {
        fakeNetworkMonitor.setOnline(online = false)
        fakePriceAlertDao.insert(
            PriceAlertEntity(stationId = 1, lastNotifiedPrice = 1.50, isSynced = true),
        )

        sut.removePriceAlert(stationId = 1)

        assertTrue(fakeSupabaseManager.removedAlerts.isEmpty())
        val pendingDeletes = fakePriceAlertDao.getPendingDeletes()
        assertEquals(1, pendingDeletes.size)
        assertEquals(ModificationType.DELETE, pendingDeletes.first().typeModification)
    }

    @Test
    fun removePriceAlert_whenLastAlert_disablesNotifications() = runTest {
        fakeNetworkMonitor.setOnline(online = true)
        fakePriceAlertDao.insert(
            PriceAlertEntity(stationId = 1, lastNotifiedPrice = 1.50, isSynced = true),
        )

        sut.removePriceAlert(stationId = 1)

        assertTrue(fakeOneSignalManager.enabledStates.contains(false))
    }

    @Test
    fun removePriceAlert_whenAlertDoesNotExist_doesNothing() = runTest {
        sut.removePriceAlert(stationId = 999)

        assertTrue(fakeSupabaseManager.removedAlerts.isEmpty())
        assertFalse(fakePriceAlertDao.hasPendingSync())
    }

    // endregion

    // region sync

    @Test
    fun sync_withPendingInserts_syncesToSupabaseAndMarksAsSynced() = runTest {
        fakePriceAlertDao.insert(
            PriceAlertEntity(stationId = 1, lastNotifiedPrice = 1.50, isSynced = false),
        )
        fakePriceAlertDao.insert(
            PriceAlertEntity(stationId = 2, lastNotifiedPrice = 1.60, isSynced = false),
        )

        val result = sut.sync()

        assertTrue(result)
        assertTrue(fakeSupabaseManager.addedAlerts.containsAll(listOf(1, 2)))
        assertFalse(fakePriceAlertDao.hasPendingSync())
    }

    @Test
    fun sync_withPendingDeletes_removesFromSupabaseAndDeletesLocally() = runTest {
        fakePriceAlertDao.insert(
            PriceAlertEntity(
                stationId = 1,
                lastNotifiedPrice = 1.50,
                isSynced = false,
                typeModification = ModificationType.DELETE,
            ),
        )

        val result = sut.sync()

        assertTrue(result)
        assertTrue(fakeSupabaseManager.removedAlerts.contains(1))
        assertFalse(fakePriceAlertDao.hasPendingSync())
    }

    @Test
    fun sync_whenSupabaseThrows_returnsFalseAndLogsFailedEventWithExtras() = runTest {
        fakeSupabaseManager.shouldThrowOnAdd = RuntimeException("network timeout")
        fakePriceAlertDao.insert(
            PriceAlertEntity(stationId = 1, lastNotifiedPrice = 1.50, isSynced = false),
        )

        val result = sut.sync()

        assertFalse(result)
        val failedEvent = fakeAnalyticsHelper.loggedEvents.find {
            it.type == AnalyticsEvent.Types.ALERTS_SYNC_FAILED
        }
        val errorMessage = failedEvent?.extras?.find {
            it.key == AnalyticsEvent.ParamKeys.ERROR_MESSAGE
        }?.value
        val errorType = failedEvent?.extras?.find {
            it.key == AnalyticsEvent.ParamKeys.ERROR_TYPE
        }?.value
        assertEquals("network timeout", errorMessage)
        assertEquals("RuntimeException", errorType)
    }

    @Test
    fun sync_withNoPendingChanges_returnsTrue() = runTest {
        val result = sut.sync()

        assertTrue(result)
    }

    // endregion

    // region hasPendingSync

    @Test
    fun hasPendingSync_withUnsyncedAlerts_returnsTrue() = runTest {
        fakePriceAlertDao.insert(
            PriceAlertEntity(stationId = 1, lastNotifiedPrice = 1.50, isSynced = false),
        )

        assertTrue(sut.hasPendingSync())
    }

    @Test
    fun hasPendingSync_withAllAlertsSynced_returnsFalse() = runTest {
        fakePriceAlertDao.insert(
            PriceAlertEntity(stationId = 1, lastNotifiedPrice = 1.50, isSynced = true),
        )

        assertFalse(sut.hasPendingSync())
    }

    // endregion
}
