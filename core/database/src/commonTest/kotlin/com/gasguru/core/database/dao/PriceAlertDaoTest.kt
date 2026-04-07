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
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals

class PriceAlertDaoTest {

    private lateinit var priceAlertDao: PriceAlertDao
    private lateinit var db: GasGuruDatabase

    @BeforeTest
    fun createDb() {
        db = Room.databaseBuilder<GasGuruDatabase>(name = ":memory:")
            .setDriver(BundledSQLiteDriver())
            .setQueryCoroutineContext(Dispatchers.IO)
            .build()
        priceAlertDao = db.priceAlertDao()
    }

    @AfterTest
    fun closeDb() {
        db.close()
    }

    @Test
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
    fun getPendingInserts() = runTest {
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
        val alert3 = PriceAlertEntity(
            stationId = 789,
            lastNotifiedPrice = 1.70,
            typeModification = ModificationType.DELETE,
            isSynced = false,
        )

        priceAlertDao.insert(alert1)
        priceAlertDao.insert(alert2)
        priceAlertDao.insert(alert3)

        val pendingInserts = priceAlertDao.getPendingInserts()
        assertEquals(1, pendingInserts.size)
        assertEquals(123, pendingInserts[0].stationId)
    }

    @Test
    fun getPendingDeletes() = runTest {
        val alert1 = PriceAlertEntity(
            stationId = 123,
            lastNotifiedPrice = 1.50,
            typeModification = ModificationType.DELETE,
            isSynced = false,
        )
        val alert2 = PriceAlertEntity(
            stationId = 456,
            lastNotifiedPrice = 1.60,
            typeModification = ModificationType.INSERT,
            isSynced = false,
        )
        val alert3 = PriceAlertEntity(
            stationId = 789,
            lastNotifiedPrice = 1.70,
            typeModification = ModificationType.DELETE,
            isSynced = true,
        )

        priceAlertDao.insert(alert1)
        priceAlertDao.insert(alert2)
        priceAlertDao.insert(alert3)

        val pendingDeletes = priceAlertDao.getPendingDeletes()
        assertEquals(1, pendingDeletes.size)
        assertEquals(123, pendingDeletes[0].stationId)
    }

    @Test
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
    fun getActiveAlertsCount() = runTest {
        val alert1 = PriceAlertEntity(
            stationId = 123,
            lastNotifiedPrice = 1.50,
            typeModification = ModificationType.INSERT,
            isSynced = true,
        )
        val alert2 = PriceAlertEntity(
            stationId = 456,
            lastNotifiedPrice = 1.60,
            typeModification = ModificationType.INSERT,
            isSynced = false,
        )
        val alert3 = PriceAlertEntity(
            stationId = 789,
            lastNotifiedPrice = 1.70,
            typeModification = ModificationType.DELETE,
            isSynced = false,
        )

        priceAlertDao.insert(alert1)
        priceAlertDao.insert(alert2)
        priceAlertDao.insert(alert3)

        val count = priceAlertDao.getActiveAlertsCount()
        assertEquals(2, count) // alert1 and alert2 both have INSERT type, alert3 is DELETE type
    }

    @Test
    fun deleteByStationId() = runTest {
        val alert1 = PriceAlertEntity(
            stationId = 123,
            lastNotifiedPrice = 1.50,
            typeModification = ModificationType.INSERT,
            isSynced = true,
        )
        val alert2 = PriceAlertEntity(
            stationId = 123,
            lastNotifiedPrice = 1.60,
            typeModification = ModificationType.DELETE,
            isSynced = false,
        )

        priceAlertDao.insert(alert1)
        priceAlertDao.insert(alert2)

        priceAlertDao.deleteByStationId(123)

        val remainingPendingDeletes = priceAlertDao.getPendingDeletes()
        assertEquals(0, remainingPendingDeletes.size)
    }

    @Test
    fun deleteByStationId_shouldNotAffectOtherStations() = runTest {
        val alert1 = PriceAlertEntity(
            stationId = 123,
            lastNotifiedPrice = 1.50,
            typeModification = ModificationType.INSERT,
            isSynced = true,
        )
        val alert2 = PriceAlertEntity(
            stationId = 456,
            lastNotifiedPrice = 1.60,
            typeModification = ModificationType.INSERT,
            isSynced = false,
        )

        priceAlertDao.insert(alert1)
        priceAlertDao.insert(alert2)

        priceAlertDao.deleteByStationId(123)

        val remainingAlerts = priceAlertDao.getAllPriceAlerts().first()
        assertEquals(1, remainingAlerts.size)
        assertEquals(456, remainingAlerts[0].stationId)
    }

    @Test
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
    fun getByStationId_whenAlertDoesNotExist_returnsNull() = runTest {
        val result = priceAlertDao.getByStationId(stationId = 999)

        assertEquals(null, result)
    }

    @Test
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
    fun hasPendingSync_withEmptyDatabase_returnsFalse() = runTest {
        val hasPending = priceAlertDao.hasPendingSync()

        assertEquals(false, hasPending)
    }

    @Test
    fun getPendingInserts_withEmptyDatabase_returnsEmptyList() = runTest {
        val result = priceAlertDao.getPendingInserts()

        assertEquals(0, result.size)
    }

    @Test
    fun getPendingDeletes_withEmptyDatabase_returnsEmptyList() = runTest {
        val result = priceAlertDao.getPendingDeletes()

        assertEquals(0, result.size)
    }

    @Test
    fun getActiveAlertsCount_withEmptyDatabase_returnsZero() = runTest {
        val count = priceAlertDao.getActiveAlertsCount()

        assertEquals(0, count)
    }

    @Test
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
