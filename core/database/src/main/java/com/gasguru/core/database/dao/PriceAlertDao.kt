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
}