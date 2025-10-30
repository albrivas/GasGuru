package com.gasguru.core.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.gasguru.core.database.model.PriceAlertEntity

@Dao
interface PriceAlertDao {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun addPriceAlert(priceAlert: PriceAlertEntity)

    @Query("DELETE FROM price_alerts WHERE stationId = :stationId")
    suspend fun removePriceAlert(stationId: Int)

    @Query("SELECT * FROM price_alerts WHERE isSynced = 0")
    suspend fun getPendingSyncAlerts(): List<PriceAlertEntity>

    @Query("UPDATE price_alerts SET isSynced = 1 WHERE stationId = :stationId")
    suspend fun markAsSynced(stationId: Int)

    @Query("SELECT COUNT(*) FROM price_alerts WHERE isSynced = 0")
    suspend fun hasPendingSync(): Boolean

    @Query("SELECT COUNT(*) FROM price_alerts")
    suspend fun getTotalAlertsCount(): Int
}