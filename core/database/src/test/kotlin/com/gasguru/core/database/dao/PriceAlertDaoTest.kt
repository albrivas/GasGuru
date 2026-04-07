package com.gasguru.core.database.dao

import androidx.room.Room
import androidx.sqlite.driver.bundled.BundledSQLiteDriver
import app.cash.turbine.test
import com.gasguru.core.database.GasGuruDatabase
import com.gasguru.core.database.model.ModificationType
import com.gasguru.core.database.model.PriceAlertEntity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test

class PriceAlertDaoTest {

    private lateinit var priceAlertDao: PriceAlertDao
    private lateinit var db: GasGuruDatabase

    @BeforeEach
    fun createDb() {
        db = Room.inMemoryDatabaseBuilder<GasGuruDatabase>()
            .setDriver(BundledSQLiteDriver())
            .setQueryCoroutineContext(Dispatchers.IO)
            .build()
        priceAlertDao = db.priceAlertDao()
    }

    @AfterEach
    fun closeDb() {
        db.close()
    }

    @Test
    @DisplayName(
        """
        GIVEN valid price alert
        WHEN inserting
        THEN should save successfully
        """
    )
    fun insertPriceAlert() = runTest {
        val alert = PriceAlertEntity(
            stationId = 123,
            lastNotifiedPrice = 1.50,
            typeModification = ModificationType.INSERT,
            isSynced = false,
        )

        priceAlertDao.insert(alert)

        val alerts = priceAlertDao.getAllPriceAlerts().first()
        assertEquals(1, alerts.size)
        assertEquals(123, alerts[0].stationId)
        assertEquals(1.50, alerts[0].lastNotifiedPrice, 0.01)
    }

    @Test
    @DisplayName(
        """
        GIVEN existing alert
        WHEN inserting delete record
        THEN should not appear in active alerts
        """
    )
    fun insertDeleteRecord() = runTest {
        val insertAlert = PriceAlertEntity(
            stationId = 123,
            lastNotifiedPrice = 1.50,
            typeModification = ModificationType.INSERT,
            isSynced = true,
        )

        val deleteAlert = PriceAlertEntity(
            stationId = 123,
            lastNotifiedPrice = 1.50,
            typeModification = ModificationType.DELETE,
            isSynced = false,
        )

        priceAlertDao.insert(insertAlert)
        priceAlertDao.insert(deleteAlert)

        val activeAlerts = priceAlertDao.getAllPriceAlerts().first()
        assertEquals(0, activeAlerts.size)
    }

    @Test
    @DisplayName(
        """
        GIVEN mixed alerts
        WHEN getting pending inserts
        THEN should return only unsynced insert alerts
        """
    )
    fun getPendingInserts() = runTest {
        val alert1 = PriceAlertEntity(
            stationId = 123,
            lastNotifiedPrice = 1.50,
            typeModification = ModificationType.INSERT,
            isSynced = false
        )
        val alert2 = PriceAlertEntity(
            stationId = 456,
            lastNotifiedPrice = 1.60,
            typeModification = ModificationType.INSERT,
            isSynced = true
        )
        val alert3 = PriceAlertEntity(
            stationId = 789,
            lastNotifiedPrice = 1.70,
            typeModification = ModificationType.DELETE,
            isSynced = false
        )

        priceAlertDao.insert(alert1)
        priceAlertDao.insert(alert2)
        priceAlertDao.insert(alert3)

        val pendingInserts = priceAlertDao.getPendingInserts()
        assertEquals(1, pendingInserts.size)
        assertEquals(123, pendingInserts[0].stationId)
    }

    @Test
    @DisplayName(
        """
        GIVEN mixed alerts
        WHEN getting pending deletes
        THEN should return only unsynced delete alerts
        """
    )
    fun getPendingDeletes() = runTest {
        val alert1 = PriceAlertEntity(
            stationId = 123,
            lastNotifiedPrice = 1.50,
            typeModification = ModificationType.DELETE,
            isSynced = false
        )
        val alert2 = PriceAlertEntity(
            stationId = 456,
            lastNotifiedPrice = 1.60,
            typeModification = ModificationType.INSERT,
            isSynced = false
        )
        val alert3 = PriceAlertEntity(
            stationId = 789,
            lastNotifiedPrice = 1.70,
            typeModification = ModificationType.DELETE,
            isSynced = true
        )

        priceAlertDao.insert(alert1)
        priceAlertDao.insert(alert2)
        priceAlertDao.insert(alert3)

        val pendingDeletes = priceAlertDao.getPendingDeletes()
        assertEquals(1, pendingDeletes.size)
        assertEquals(123, pendingDeletes[0].stationId)
    }

    @Test
    @DisplayName(
        """
        GIVEN unsynced alert
        WHEN marking as synced
        THEN should not appear in pending inserts
        """
    )
    fun markAsSynced() = runTest {
        val alert = PriceAlertEntity(
            stationId = 123,
            lastNotifiedPrice = 1.50,
            typeModification = ModificationType.INSERT,
            isSynced = false,
        )

        priceAlertDao.insert(alert)
        priceAlertDao.markAsSynced(stationId = 123)

        val pendingInserts = priceAlertDao.getPendingInserts()
        assertEquals(0, pendingInserts.size)
    }

    @Test
    @DisplayName(
        """
        GIVEN mixed alerts
        WHEN counting total
        THEN should return only active alerts count
        """
    )
    fun getActiveAlertsCount() = runTest {
        val alert1 = PriceAlertEntity(
            stationId = 123,
            lastNotifiedPrice = 1.50,
            typeModification = ModificationType.INSERT,
            isSynced = true
        )
        val alert2 = PriceAlertEntity(
            stationId = 456,
            lastNotifiedPrice = 1.60,
            typeModification = ModificationType.INSERT,
            isSynced = false
        )
        val alert3 = PriceAlertEntity(
            stationId = 789,
            lastNotifiedPrice = 1.70,
            typeModification = ModificationType.DELETE,
            isSynced = false
        )

        priceAlertDao.insert(alert1)
        priceAlertDao.insert(alert2)
        priceAlertDao.insert(alert3)

        val count = priceAlertDao.getActiveAlertsCount()
        assertEquals(2, count) // alert1 and alert2 both have INSERT type, alert3 is DELETE type
    }

    @Test
    @DisplayName(
        """
        GIVEN alerts
        WHEN deleting by station ID
        THEN should remove all records for that station
        """
    )
    fun deleteByStationId() = runTest {
        val alert1 = PriceAlertEntity(
            stationId = 123,
            lastNotifiedPrice = 1.50,
            typeModification = ModificationType.INSERT,
            isSynced = true
        )
        val alert2 = PriceAlertEntity(
            stationId = 123,
            lastNotifiedPrice = 1.60,
            typeModification = ModificationType.DELETE,
            isSynced = false
        )

        priceAlertDao.insert(alert1)
        priceAlertDao.insert(alert2)

        priceAlertDao.deleteByStationId(123)

        val remainingPendingDeletes = priceAlertDao.getPendingDeletes()
        assertEquals(0, remainingPendingDeletes.size)
    }

    @Test
    @DisplayName(
        """
        GIVEN active alerts
        WHEN deleting by station ID
        THEN should not affect other stations
        """
    )
    fun deleteByStationId_shouldNotAffectOtherStations() = runTest {
        val alert1 = PriceAlertEntity(
            stationId = 123,
            lastNotifiedPrice = 1.50,
            typeModification = ModificationType.INSERT,
            isSynced = true
        )
        val alert2 = PriceAlertEntity(
            stationId = 456,
            lastNotifiedPrice = 1.60,
            typeModification = ModificationType.INSERT,
            isSynced = false
        )

        priceAlertDao.insert(alert1)
        priceAlertDao.insert(alert2)

        priceAlertDao.deleteByStationId(123)

        val remainingAlerts = priceAlertDao.getAllPriceAlerts().first()
        assertEquals(1, remainingAlerts.size)
        assertEquals(456, remainingAlerts[0].stationId)
    }

    @Test
    @DisplayName(
        """
        GIVEN price alerts flow
        WHEN inserting and deleting
        THEN should emit correct updates
        """
    )
    fun getAllPriceAlertsFlow() = runTest {
        priceAlertDao.getAllPriceAlerts().test {
            assertEquals(0, awaitItem().size)

            val alert1 = PriceAlertEntity(
                stationId = 123,
                lastNotifiedPrice = 1.50,
                typeModification = ModificationType.INSERT,
                isSynced = false,
            )
            priceAlertDao.insert(alert1)
            assertEquals(1, awaitItem().size)

            val deleteAlert = PriceAlertEntity(
                stationId = 123,
                lastNotifiedPrice = 1.50,
                typeModification = ModificationType.DELETE,
                isSynced = false,
            )
            priceAlertDao.insert(deleteAlert)
            assertEquals(0, awaitItem().size)
        }
    }

    @Test
    @DisplayName(
        """
        GIVEN duplicate station ID
        WHEN inserting
        THEN should replace existing record
        """
    )
    fun handleDuplicateStationIds() = runTest {
        val alert1 = PriceAlertEntity(
            stationId = 123,
            lastNotifiedPrice = 1.50,
            typeModification = ModificationType.INSERT,
            isSynced = false,
        )
        val alert2 = PriceAlertEntity(
            stationId = 123,
            lastNotifiedPrice = 1.60,
            typeModification = ModificationType.INSERT,
            isSynced = false,
        )

        priceAlertDao.insert(alert1)
        priceAlertDao.insert(alert2)

        val alerts = priceAlertDao.getAllPriceAlerts().first()
        assertEquals(1, alerts.size)
        assertEquals(1.60, alerts[0].lastNotifiedPrice, 0.01)
    }

    @Test
    @DisplayName(
        """
        GIVEN alerts
        WHEN checking has pending sync
        THEN should return correct status
        """
    )
    fun hasPendingSync() = runTest {
        val alert1 = PriceAlertEntity(
            stationId = 123,
            lastNotifiedPrice = 1.50,
            typeModification = ModificationType.INSERT,
            isSynced = false,
        )
        val alert2 = PriceAlertEntity(
            stationId = 456,
            lastNotifiedPrice = 1.60,
            typeModification = ModificationType.INSERT,
            isSynced = true,
        )

        priceAlertDao.insert(alert1)
        priceAlertDao.insert(alert2)

        val hasPending = priceAlertDao.hasPendingSync()
        assertEquals(true, hasPending)

        priceAlertDao.markAsSynced(123)

        val hasPendingAfterSync = priceAlertDao.hasPendingSync()
        assertEquals(false, hasPendingAfterSync)
    }

    @Test
    @DisplayName(
        """GIVEN an existing price alert
        WHEN querying getByStationId
        THEN returns the matching entity"""
    )
    fun getByStationId_whenAlertExists_returnsEntity() = runTest {
        val alert = PriceAlertEntity(
            stationId = 100,
            lastNotifiedPrice = 1.75,
            typeModification = ModificationType.INSERT,
            isSynced = false,
        )
        priceAlertDao.insert(alert)

        val result = priceAlertDao.getByStationId(stationId = 100)

        assertEquals(100, result?.stationId)
        assertEquals(1.75, result?.lastNotifiedPrice ?: 0.0, 0.01)
        assertEquals(ModificationType.INSERT, result?.typeModification)
    }

    @Test
    @DisplayName(
        """GIVEN no alerts in database
        WHEN querying getByStationId
        THEN returns null"""
    )
    fun getByStationId_whenAlertDoesNotExist_returnsNull() = runTest {
        val result = priceAlertDao.getByStationId(stationId = 999)

        assertEquals(null, result)
    }

    @Test
    @DisplayName(
        """GIVEN an alert that was replaced by a DELETE record
        WHEN querying getByStationId
        THEN returns the latest (DELETE) record"""
    )
    fun getByStationId_afterReplaceWithDeleteRecord_returnsDeleteRecord() = runTest {
        val insertAlert = PriceAlertEntity(
            stationId = 200,
            lastNotifiedPrice = 1.50,
            typeModification = ModificationType.INSERT,
            isSynced = true,
        )
        val deleteAlert = PriceAlertEntity(
            stationId = 200,
            lastNotifiedPrice = 1.50,
            typeModification = ModificationType.DELETE,
            isSynced = false,
        )
        priceAlertDao.insert(insertAlert)
        priceAlertDao.insert(deleteAlert)

        val result = priceAlertDao.getByStationId(stationId = 200)

        assertEquals(ModificationType.DELETE, result?.typeModification)
    }

    @Test
    @DisplayName(
        """GIVEN an alert that was deleted by stationId
        WHEN querying getByStationId
        THEN returns null"""
    )
    fun getByStationId_afterDeleteByStationId_returnsNull() = runTest {
        val alert = PriceAlertEntity(
            stationId = 300,
            lastNotifiedPrice = 1.40,
            typeModification = ModificationType.INSERT,
            isSynced = false,
        )
        priceAlertDao.insert(alert)
        priceAlertDao.deleteByStationId(stationId = 300)

        val result = priceAlertDao.getByStationId(stationId = 300)

        assertEquals(null, result)
    }

    @Test
    @DisplayName(
        """GIVEN empty database
        WHEN checking hasPendingSync
        THEN returns false"""
    )
    fun hasPendingSync_withEmptyDatabase_returnsFalse() = runTest {
        val hasPending = priceAlertDao.hasPendingSync()

        assertEquals(false, hasPending)
    }

    @Test
    @DisplayName(
        """GIVEN empty database
        WHEN calling getPendingInserts
        THEN returns empty list"""
    )
    fun getPendingInserts_withEmptyDatabase_returnsEmptyList() = runTest {
        val result = priceAlertDao.getPendingInserts()

        assertEquals(0, result.size)
    }

    @Test
    @DisplayName(
        """GIVEN empty database
        WHEN calling getPendingDeletes
        THEN returns empty list"""
    )
    fun getPendingDeletes_withEmptyDatabase_returnsEmptyList() = runTest {
        val result = priceAlertDao.getPendingDeletes()

        assertEquals(0, result.size)
    }

    @Test
    @DisplayName(
        """GIVEN empty database
        WHEN calling getActiveAlertsCount
        THEN returns 0"""
    )
    fun getActiveAlertsCount_withEmptyDatabase_returnsZero() = runTest {
        val count = priceAlertDao.getActiveAlertsCount()

        assertEquals(0, count)
    }

    @Test
    @DisplayName(
        """GIVEN only synced INSERT alerts
        WHEN calling getPendingInserts
        THEN returns empty list"""
    )
    fun getPendingInserts_withAllSynced_returnsEmptyList() = runTest {
        priceAlertDao.insert(
            PriceAlertEntity(
                stationId = 11,
                lastNotifiedPrice = 1.50,
                typeModification = ModificationType.INSERT,
                isSynced = true,
            )
        )

        val result = priceAlertDao.getPendingInserts()

        assertEquals(0, result.size)
    }
}
