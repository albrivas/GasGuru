package com.gasguru.core.database.dao

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import app.cash.turbine.test
import com.gasguru.core.database.GasGuruDatabase
import com.gasguru.core.database.model.ModificationType
import com.gasguru.core.database.model.PriceAlertEntity
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
        val context = ApplicationProvider.getApplicationContext<Context>()
        db = Room.inMemoryDatabaseBuilder(
            context,
            GasGuruDatabase::class.java
        ).build()
        priceAlertDao = db.priceAlertDao()
    }

    @AfterEach
    fun closeDb() {
        db.close()
    }

    @Test
    @DisplayName("GIVEN valid price alert WHEN inserting THEN should save successfully")
    fun insertPriceAlert() = runTest {
        val alert = PriceAlertEntity(
            stationId = 123,
            lastNotifiedPrice = 1.50,
            typeModification = ModificationType.INSERT,
            isSynced = false
        )

        priceAlertDao.insert(alert)

        val alerts = priceAlertDao.getAllPriceAlerts().first()
        assertEquals(1, alerts.size)
        assertEquals(123, alerts[0].stationId)
        assertEquals(1.50, alerts[0].lastNotifiedPrice, 0.01)
    }

    @Test
    @DisplayName("GIVEN existing alert WHEN inserting delete record THEN should not appear in active alerts")
    fun insertDeleteRecord() = runTest {
        val insertAlert = PriceAlertEntity(
            stationId = 123,
            lastNotifiedPrice = 1.50,
            typeModification = ModificationType.INSERT,
            isSynced = true
        )

        val deleteAlert = PriceAlertEntity(
            stationId = 123,
            lastNotifiedPrice = 1.50,
            typeModification = ModificationType.DELETE,
            isSynced = false
        )

        priceAlertDao.insert(insertAlert)
        priceAlertDao.insert(deleteAlert)

        val activeAlerts = priceAlertDao.getAllPriceAlerts().first()
        assertEquals(0, activeAlerts.size)
    }

    @Test
    @DisplayName("GIVEN mixed alerts WHEN getting pending inserts THEN should return only unsynced insert alerts")
    fun getPendingInserts() = runTest {
        val alert1 = PriceAlertEntity(stationId = 123, lastNotifiedPrice = 1.50, typeModification = ModificationType.INSERT, isSynced = false)
        val alert2 = PriceAlertEntity(stationId = 456, lastNotifiedPrice = 1.60, typeModification = ModificationType.INSERT, isSynced = true)
        val alert3 = PriceAlertEntity(stationId = 789, lastNotifiedPrice = 1.70, typeModification = ModificationType.DELETE, isSynced = false)

        priceAlertDao.insert(alert1)
        priceAlertDao.insert(alert2)
        priceAlertDao.insert(alert3)

        val pendingInserts = priceAlertDao.getPendingInserts()
        assertEquals(1, pendingInserts.size)
        assertEquals(123, pendingInserts[0].stationId)
    }

    @Test
    @DisplayName("GIVEN mixed alerts WHEN getting pending deletes THEN should return only unsynced delete alerts")
    fun getPendingDeletes() = runTest {
        val alert1 = PriceAlertEntity(stationId = 123, lastNotifiedPrice = 1.50, typeModification = ModificationType.DELETE, isSynced = false)
        val alert2 = PriceAlertEntity(stationId = 456, lastNotifiedPrice = 1.60, typeModification = ModificationType.INSERT, isSynced = false)
        val alert3 = PriceAlertEntity(stationId = 789, lastNotifiedPrice = 1.70, typeModification = ModificationType.DELETE, isSynced = true)

        priceAlertDao.insert(alert1)
        priceAlertDao.insert(alert2)
        priceAlertDao.insert(alert3)

        val pendingDeletes = priceAlertDao.getPendingDeletes()
        assertEquals(1, pendingDeletes.size)
        assertEquals(123, pendingDeletes[0].stationId)
    }

    @Test
    @DisplayName("GIVEN unsynced alert WHEN marking as synced THEN should not appear in pending inserts")
    fun markAsSynced() = runTest {
        val alert = PriceAlertEntity(
            stationId = 123,
            lastNotifiedPrice = 1.50,
            typeModification = ModificationType.INSERT,
            isSynced = false
        )

        priceAlertDao.insert(alert)
        priceAlertDao.markAsSynced(stationId = 123)

        val pendingInserts = priceAlertDao.getPendingInserts()
        assertEquals(0, pendingInserts.size)
    }

    @Test
    @DisplayName("GIVEN mixed alerts WHEN counting total THEN should return only active alerts count")
    fun getActiveAlertsCount() = runTest {
        val alert1 = PriceAlertEntity(stationId = 123, lastNotifiedPrice = 1.50, typeModification = ModificationType.INSERT, isSynced = true)
        val alert2 = PriceAlertEntity(stationId = 456, lastNotifiedPrice = 1.60, typeModification = ModificationType.INSERT, isSynced = false)
        val alert3 = PriceAlertEntity(stationId = 789, lastNotifiedPrice = 1.70, typeModification = ModificationType.DELETE, isSynced = false)

        priceAlertDao.insert(alert1)
        priceAlertDao.insert(alert2)
        priceAlertDao.insert(alert3)

        val count = priceAlertDao.getActiveAlertsCount()
        assertEquals(2, count) // alert1 and alert2 both have INSERT type, alert3 is DELETE type
    }

    @Test
    @DisplayName("GIVEN alerts WHEN deleting by station ID THEN should remove all records for that station")
    fun deleteByStationId() = runTest {
        val alert1 = PriceAlertEntity(stationId = 123, lastNotifiedPrice = 1.50, typeModification = ModificationType.INSERT, isSynced = true)
        val alert2 = PriceAlertEntity(stationId = 123, lastNotifiedPrice = 1.60, typeModification = ModificationType.DELETE, isSynced = false)

        priceAlertDao.insert(alert1)
        priceAlertDao.insert(alert2)

        priceAlertDao.deleteByStationId(123)

        val remainingPendingDeletes = priceAlertDao.getPendingDeletes()
        assertEquals(0, remainingPendingDeletes.size)
    }

    @Test
    @DisplayName("GIVEN active alerts WHEN deleting by station ID THEN should not affect other stations")
    fun deleteByStationId_shouldNotAffectOtherStations() = runTest {
        val alert1 = PriceAlertEntity(stationId = 123, lastNotifiedPrice = 1.50, typeModification = ModificationType.INSERT, isSynced = true)
        val alert2 = PriceAlertEntity(stationId = 456, lastNotifiedPrice = 1.60, typeModification = ModificationType.INSERT, isSynced = false)

        priceAlertDao.insert(alert1)
        priceAlertDao.insert(alert2)

        priceAlertDao.deleteByStationId(123)

        val remainingAlerts = priceAlertDao.getAllPriceAlerts().first()
        assertEquals(1, remainingAlerts.size)
        assertEquals(456, remainingAlerts[0].stationId)
    }

    @Test
    @DisplayName("GIVEN price alerts flow WHEN inserting and deleting THEN should emit correct updates")
    fun getAllPriceAlertsFlow() = runTest {
        priceAlertDao.getAllPriceAlerts().test {
            assertEquals(0, awaitItem().size)

            val alert1 = PriceAlertEntity(
                stationId = 123, 
                lastNotifiedPrice = 1.50,
                typeModification = ModificationType.INSERT,
                isSynced = false
            )
            priceAlertDao.insert(alert1)
            assertEquals(1, awaitItem().size)

            val deleteAlert = PriceAlertEntity(
                stationId = 123, 
                lastNotifiedPrice = 1.50, 
                typeModification = ModificationType.DELETE,
                isSynced = false
            )
            priceAlertDao.insert(deleteAlert)
            assertEquals(0, awaitItem().size)
        }
    }

    @Test
    @DisplayName("GIVEN duplicate station ID WHEN inserting THEN should replace existing record")
    fun handleDuplicateStationIds() = runTest {
        val alert1 = PriceAlertEntity(
            stationId = 123, 
            lastNotifiedPrice = 1.50,
            typeModification = ModificationType.INSERT,
            isSynced = false
        )
        val alert2 = PriceAlertEntity(
            stationId = 123, 
            lastNotifiedPrice = 1.60,
            typeModification = ModificationType.INSERT,
            isSynced = false
        )

        priceAlertDao.insert(alert1)
        priceAlertDao.insert(alert2)

        val alerts = priceAlertDao.getAllPriceAlerts().first()
        assertEquals(1, alerts.size)
        assertEquals(1.60, alerts[0].lastNotifiedPrice, 0.01)
    }

    @Test
    @DisplayName("GIVEN alerts WHEN checking has pending sync THEN should return correct status")
    fun hasPendingSync() = runTest {
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

        priceAlertDao.insert(alert1)
        priceAlertDao.insert(alert2)

        val hasPending = priceAlertDao.hasPendingSync()
        assertEquals(true, hasPending)

        priceAlertDao.markAsSynced(123)

        val hasPendingAfterSync = priceAlertDao.hasPendingSync()
        assertEquals(false, hasPendingAfterSync)
    }
}