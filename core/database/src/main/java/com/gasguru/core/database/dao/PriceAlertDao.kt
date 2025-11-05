package com.gasguru.core.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.gasguru.core.database.model.PriceAlertEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface PriceAlertDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(priceAlert: PriceAlertEntity)

    @Query("SELECT * FROM price_alerts WHERE typeModification = 'INSERT' AND isSynced = 0")
    suspend fun getPendingInserts(): List<PriceAlertEntity>

    @Query("SELECT * FROM price_alerts WHERE typeModification = 'DELETE' AND isSynced = 0")
    suspend fun getPendingDeletes(): List<PriceAlertEntity>

    @Query("UPDATE price_alerts SET isSynced = 1 WHERE stationId = :stationId")
    suspend fun markAsSynced(stationId: Int)

    @Query("SELECT COUNT(*) FROM price_alerts WHERE isSynced = 0")
    suspend fun hasPendingSync(): Boolean

    @Query(
        """
        SELECT COUNT(*) FROM price_alerts 
        WHERE typeModification = 'INSERT'
        AND stationId NOT IN (
            SELECT stationId FROM price_alerts 
            WHERE typeModification = 'DELETE' AND isSynced = 0
        )
    """
    )
    suspend fun getActiveAlertsCount(): Int

    @Query(
        """
        SELECT * FROM price_alerts 
        WHERE typeModification = 'INSERT'
        AND stationId NOT IN (
            SELECT stationId FROM price_alerts 
            WHERE typeModification = 'DELETE' AND isSynced = 0
        )
    """
    )
    fun getAllPriceAlerts(): Flow<List<PriceAlertEntity>>

    @Query("SELECT * FROM price_alerts WHERE stationId = :stationId")
    suspend fun getByStationId(stationId: Int): PriceAlertEntity?

    @Query("DELETE FROM price_alerts WHERE stationId = :stationId")
    suspend fun deleteByStationId(stationId: Int)
}
