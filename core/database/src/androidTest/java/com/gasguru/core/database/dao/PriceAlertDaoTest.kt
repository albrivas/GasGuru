package com.gasguru.core.database.dao

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import app.cash.turbine.test
import com.gasguru.core.database.GasGuruDatabase
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
    @DisplayName("GIVEN valid price alert WHEN adding THEN should save successfully")
    fun addPriceAlert() = runTest {
        val alert = PriceAlertEntity(
            stationId = 123,
            lastNotifiedPrice = 1.50,
            isDeleted = false,
            isSynced = false
        )

        priceAlertDao.addPriceAlert(alert)

        val alerts = priceAlertDao.getAllPriceAlerts().first()
        assertEquals(1, alerts.size)
        assertEquals(123, alerts[0].stationId)
        assertEquals(1.50, alerts[0].lastNotifiedPrice, 0.01)
    }

    @Test
    @DisplayName("GIVEN existing alert WHEN marking as deleted THEN should not appear in active alerts")
    fun markAsDeleted() = runTest {
        val alert = PriceAlertEntity(
            stationId = 123,
            lastNotifiedPrice = 1.50,
            isDeleted = false,
            isSynced = true
        )

        priceAlertDao.addPriceAlert(alert)
        priceAlertDao.markAsDeleted(stationId = 123)

        val activeAlerts = priceAlertDao.getAllPriceAlerts().first()
        assertEquals(0, activeAlerts.size)
    }

    @Test
    @DisplayName("GIVEN mixed alerts WHEN getting pending adds THEN should return only unsynced active alerts")
    fun getPendingAddAlerts() = runTest {
        val alert1 = PriceAlertEntity(stationId = 123, lastNotifiedPrice = 1.50, isDeleted = false, isSynced = false)
        val alert2 = PriceAlertEntity(stationId = 456, lastNotifiedPrice = 1.60, isDeleted = false, isSynced = true)
        val alert3 = PriceAlertEntity(stationId = 789, lastNotifiedPrice = 1.70, isDeleted = true, isSynced = false)

        priceAlertDao.addPriceAlert(alert1)
        priceAlertDao.addPriceAlert(alert2)
        priceAlertDao.addPriceAlert(alert3)

        val pendingAdds = priceAlertDao.getPendingAddAlerts()
        assertEquals(1, pendingAdds.size)
        assertEquals(123, pendingAdds[0].stationId)
    }

    @Test
    @DisplayName("GIVEN mixed alerts WHEN getting pending deletes THEN should return only unsynced deleted alerts")
    fun getPendingDeleteAlerts() = runTest {
        val alert1 = PriceAlertEntity(stationId = 123, lastNotifiedPrice = 1.50, isDeleted = true, isSynced = false)
        val alert2 = PriceAlertEntity(stationId = 456, lastNotifiedPrice = 1.60, isDeleted = false, isSynced = false)
        val alert3 = PriceAlertEntity(stationId = 789, lastNotifiedPrice = 1.70, isDeleted = true, isSynced = true)

        priceAlertDao.addPriceAlert(alert1)
        priceAlertDao.addPriceAlert(alert2)
        priceAlertDao.addPriceAlert(alert3)

        val pendingDeletes = priceAlertDao.getPendingDeleteAlerts()
        assertEquals(1, pendingDeletes.size)
        assertEquals(123, pendingDeletes[0].stationId)
    }

    @Test
    @DisplayName("GIVEN unsynced alert WHEN marking as synced THEN should not appear in pending adds")
    fun markAsSynced() = runTest {
        val alert = PriceAlertEntity(
            stationId = 123,
            lastNotifiedPrice = 1.50,
            isDeleted = false,
            isSynced = false
        )

        priceAlertDao.addPriceAlert(alert)
        priceAlertDao.markAsSynced(stationId = 123)

        val pendingAdds = priceAlertDao.getPendingAddAlerts()
        assertEquals(0, pendingAdds.size)
    }

    @Test
    @DisplayName("GIVEN mixed alerts WHEN counting total THEN should return only active alerts count")
    fun getActiveAlertsCount() = runTest {
        val alert1 = PriceAlertEntity(stationId = 123, lastNotifiedPrice = 1.50, isDeleted = false, isSynced = true)
        val alert2 = PriceAlertEntity(stationId = 456, lastNotifiedPrice = 1.60, isDeleted = false, isSynced = false)
        val alert3 = PriceAlertEntity(stationId = 789, lastNotifiedPrice = 1.70, isDeleted = true, isSynced = false)

        priceAlertDao.addPriceAlert(alert1)
        priceAlertDao.addPriceAlert(alert2)
        priceAlertDao.addPriceAlert(alert3)

        val count = priceAlertDao.getActiveAlertsCount()
        assertEquals(2, count)
    }

    @Test
    @DisplayName("GIVEN deleted alerts WHEN cleaning up THEN should remove all deleted alerts")
    fun cleanupSyncedDeletes() = runTest {
        val alert1 = PriceAlertEntity(stationId = 123, lastNotifiedPrice = 1.50, isDeleted = true, isSynced = true)
        val alert2 = PriceAlertEntity(stationId = 456, lastNotifiedPrice = 1.60, isDeleted = true, isSynced = false)

        priceAlertDao.addPriceAlert(alert1)
        priceAlertDao.addPriceAlert(alert2)

        priceAlertDao.cleanupSyncedDeletes()

        val pendingDeletes = priceAlertDao.getPendingDeleteAlerts()
        assertEquals(0, pendingDeletes.size)
    }

    @Test
    @DisplayName("GIVEN active alerts WHEN cleaning up THEN should not remove active alerts")
    fun cleanupSyncedDeletes_shouldNotDeleteActiveAlerts() = runTest {
        val alert1 = PriceAlertEntity(stationId = 123, lastNotifiedPrice = 1.50, isDeleted = false, isSynced = true)
        val alert2 = PriceAlertEntity(stationId = 456, lastNotifiedPrice = 1.60, isDeleted = false, isSynced = false)

        priceAlertDao.addPriceAlert(alert1)
        priceAlertDao.addPriceAlert(alert2)

        priceAlertDao.cleanupSyncedDeletes()

        val remainingAlerts = priceAlertDao.getAllPriceAlerts().first()
        assertEquals(2, remainingAlerts.size)
    }

    @Test
    @DisplayName("GIVEN price alerts flow WHEN adding and deleting THEN should emit correct updates")
    fun getAllPriceAlertsFlow() = runTest {
        priceAlertDao.getAllPriceAlerts().test {
            assertEquals(0, awaitItem().size)

            val alert1 = PriceAlertEntity(stationId = 123, lastNotifiedPrice = 1.50)
            priceAlertDao.addPriceAlert(alert1)
            assertEquals(1, awaitItem().size)

            priceAlertDao.markAsDeleted(stationId = 123)
            assertEquals(0, awaitItem().size)
        }
    }

    @Test
    @DisplayName("GIVEN duplicate station ID WHEN adding THEN should ignore second insert")
    fun handleDuplicateStationIds() = runTest {
        val alert1 = PriceAlertEntity(stationId = 123, lastNotifiedPrice = 1.50)
        val alert2 = PriceAlertEntity(stationId = 123, lastNotifiedPrice = 1.60)

        priceAlertDao.addPriceAlert(alert1)
        priceAlertDao.addPriceAlert(alert2)

        val alerts = priceAlertDao.getAllPriceAlerts().first()
        assertEquals(1, alerts.size)
        assertEquals(1.50, alerts[0].lastNotifiedPrice, 0.01)
    }
}