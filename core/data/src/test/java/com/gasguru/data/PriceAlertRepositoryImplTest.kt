package com.gasguru.data

import com.gasguru.core.analytics.AnalyticsEvent
import com.gasguru.core.data.repository.alerts.PriceAlertRepositoryImpl
import com.gasguru.core.database.model.ModificationType
import com.gasguru.core.database.model.PriceAlertEntity
import com.gasguru.core.database.model.VehicleEntity
import com.gasguru.core.model.data.FuelType
import com.gasguru.core.model.data.VehicleType
import com.gasguru.core.testing.CoroutinesTestExtension
import com.gasguru.core.testing.fakes.data.database.FakePriceAlertDao
import com.gasguru.core.testing.fakes.data.database.FakeVehicleDao
import com.gasguru.core.testing.fakes.data.network.FakeNetworkMonitor
import com.gasguru.core.testing.fakes.analytics.FakeAnalyticsHelper
import com.gasguru.data.fakes.FakeOneSignalManager
import com.gasguru.data.fakes.FakeSupabaseManager
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith

@ExtendWith(CoroutinesTestExtension::class)
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

    @BeforeEach
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

    @Nested
    @DisplayName("addPriceAlert")
    inner class AddPriceAlert {

        @Test
        @DisplayName(
            """
            GIVEN no alerts exist
            WHEN addPriceAlert is called
            THEN enables notifications and inserts alert locally
            """
        )
        fun enablesNotificationsAndInsertsAlertOnFirstAlert() = runTest {
            sut.addPriceAlert(stationId = 1, lastNotifiedPrice = 1.50)

            assertTrue(fakeOneSignalManager.enabledStates.contains(true))
            assertTrue(fakeSupabaseManager.addedAlerts.contains(1))
        }

        @Test
        @DisplayName(
            """
            GIVEN alerts already exist
            WHEN addPriceAlert is called
            THEN does not re-enable notifications
            """
        )
        fun doesNotReEnableNotificationsWhenAlertsAlreadyExist() = runTest {
            fakePriceAlertDao.insert(
                PriceAlertEntity(stationId = 99, lastNotifiedPrice = 1.40, isSynced = true),
            )

            sut.addPriceAlert(stationId = 1, lastNotifiedPrice = 1.50)

            assertFalse(fakeOneSignalManager.enabledStates.contains(true))
        }

        @Test
        @DisplayName(
            """
            GIVEN device is online
            WHEN addPriceAlert is called
            THEN syncs alert to supabase and marks as synced
            """
        )
        fun syncesToSupabaseWhenOnline() = runTest {
            fakeNetworkMonitor.setOnline(online = true)

            sut.addPriceAlert(stationId = 1, lastNotifiedPrice = 1.50)

            assertTrue(fakeSupabaseManager.addedAlerts.contains(1))
            assertFalse(fakePriceAlertDao.hasPendingSync())
        }

        @Test
        @DisplayName(
            """
            GIVEN device is offline
            WHEN addPriceAlert is called
            THEN inserts locally without syncing to supabase
            """
        )
        fun insertsLocallyWithoutSyncingWhenOffline() = runTest {
            fakeNetworkMonitor.setOnline(online = false)

            sut.addPriceAlert(stationId = 1, lastNotifiedPrice = 1.50)

            assertTrue(fakeSupabaseManager.addedAlerts.isEmpty())
            assertTrue(fakePriceAlertDao.hasPendingSync())
        }
    }

    @Nested
    @DisplayName("removePriceAlert")
    inner class RemovePriceAlert {

        @Test
        @DisplayName(
            """
            GIVEN alert exists and is not synced
            WHEN removePriceAlert is called
            THEN deletes alert locally
            """
        )
        fun deletesLocallyWhenAlertNotSynced() = runTest {
            fakePriceAlertDao.insert(
                PriceAlertEntity(stationId = 1, lastNotifiedPrice = 1.50, isSynced = false),
            )

            sut.removePriceAlert(stationId = 1)

            assertFalse(fakePriceAlertDao.hasPendingSync())
            assertTrue(fakeSupabaseManager.removedAlerts.isEmpty())
        }

        @Test
        @DisplayName(
            """
            GIVEN alert is synced and device is online
            WHEN removePriceAlert is called
            THEN deletes locally and removes from supabase
            """
        )
        fun deletesLocallyAndFromSupabaseWhenSyncedAndOnline() = runTest {
            fakeNetworkMonitor.setOnline(online = true)
            fakePriceAlertDao.insert(
                PriceAlertEntity(stationId = 1, lastNotifiedPrice = 1.50, isSynced = true),
            )

            sut.removePriceAlert(stationId = 1)

            assertTrue(fakeSupabaseManager.removedAlerts.contains(1))
            assertFalse(fakePriceAlertDao.hasPendingSync())
        }

        @Test
        @DisplayName(
            """
            GIVEN alert is synced and device is offline
            WHEN removePriceAlert is called
            THEN marks alert as DELETE pending
            """
        )
        fun marksAsDeletePendingWhenSyncedAndOffline() = runTest {
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
        @DisplayName(
            """
            GIVEN it was the last active alert
            WHEN removePriceAlert is called
            THEN disables notifications
            """
        )
        fun disablesNotificationsWhenLastAlertRemoved() = runTest {
            fakeNetworkMonitor.setOnline(online = true)
            fakePriceAlertDao.insert(
                PriceAlertEntity(stationId = 1, lastNotifiedPrice = 1.50, isSynced = true),
            )

            sut.removePriceAlert(stationId = 1)

            assertTrue(fakeOneSignalManager.enabledStates.contains(false))
        }

        @Test
        @DisplayName(
            """
            GIVEN alert does not exist
            WHEN removePriceAlert is called
            THEN does nothing
            """
        )
        fun doesNothingWhenAlertDoesNotExist() = runTest {
            sut.removePriceAlert(stationId = 999)

            assertTrue(fakeSupabaseManager.removedAlerts.isEmpty())
            assertFalse(fakePriceAlertDao.hasPendingSync())
        }
    }

    @Nested
    @DisplayName("sync")
    inner class Sync {

        @Test
        @DisplayName(
            """
            GIVEN pending inserts exist
            WHEN sync is called
            THEN syncs inserts to supabase and marks as synced
            """
        )
        fun syncsPendingInserts() = runTest {
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
        @DisplayName(
            """
            GIVEN pending deletes exist
            WHEN sync is called
            THEN removes from supabase and deletes locally
            """
        )
        fun syncsPendingDeletes() = runTest {
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
        @DisplayName(
            """
            GIVEN supabase throws an exception
            WHEN sync is called
            THEN returns false and logs FAILED event with error_message and error_type extras
            """
        )
        fun returnsFalseAndLogsFailedWithExtrasWhenSupabaseThrows() = runTest {
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
        @DisplayName(
            """
            GIVEN no pending changes exist
            WHEN sync is called
            THEN returns true
            """
        )
        fun returnsTrueWhenNoPendingChanges() = runTest {
            val result = sut.sync()

            assertTrue(result)
        }
    }

    @Nested
    @DisplayName("hasPendingSync")
    inner class HasPendingSync {

        @Test
        @DisplayName(
            """
            GIVEN there are unsynced alerts
            WHEN hasPendingSync is called
            THEN returns true
            """
        )
        fun returnsTrueWhenPendingAlertsExist() = runTest {
            fakePriceAlertDao.insert(
                PriceAlertEntity(stationId = 1, lastNotifiedPrice = 1.50, isSynced = false),
            )

            assertTrue(sut.hasPendingSync())
        }

        @Test
        @DisplayName(
            """
            GIVEN all alerts are synced
            WHEN hasPendingSync is called
            THEN returns false
            """
        )
        fun returnsFalseWhenNoAlertsArePending() = runTest {
            fakePriceAlertDao.insert(
                PriceAlertEntity(stationId = 1, lastNotifiedPrice = 1.50, isSynced = true),
            )

            assertFalse(sut.hasPendingSync())
        }
    }
}
