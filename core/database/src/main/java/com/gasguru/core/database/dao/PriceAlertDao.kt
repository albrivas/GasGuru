package com.gasguru.core.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.gasguru.core.database.model.PriceAlertEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface PriceAlertDao {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun addPriceAlert(priceAlert: PriceAlertEntity)

    @Query("UPDATE price_alerts SET isDeleted = 1 WHERE stationId = :stationId")
    suspend fun markAsDeleted(stationId: Int)

    @Query("SELECT * FROM price_alerts WHERE isDeleted = 0 AND isSynced = 0")
    suspend fun getPendingAddAlerts(): List<PriceAlertEntity>

    @Query("SELECT * FROM price_alerts WHERE isDeleted = 1 AND isSynced = 0")
    suspend fun getPendingDeleteAlerts(): List<PriceAlertEntity>

    @Query("UPDATE price_alerts SET isSynced = 1 WHERE stationId = :stationId")
    suspend fun markAsSynced(stationId: Int)

    @Query("SELECT COUNT(*) FROM price_alerts WHERE isSynced = 0")
    suspend fun hasPendingSync(): Boolean

    @Query("SELECT COUNT(*) FROM price_alerts WHERE isDeleted = 0")
    suspend fun getActiveAlertsCount(): Int

    @Query("SELECT * FROM price_alerts WHERE isDeleted = 0")
    fun getAllPriceAlerts(): Flow<List<PriceAlertEntity>>

    @Query("DELETE FROM price_alerts WHERE isDeleted = 1")
    suspend fun cleanupSyncedDeletes()
}
