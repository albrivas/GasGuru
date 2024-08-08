package com.albrivas.fuelpump.core.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.albrivas.fuelpump.core.database.model.FuelStationEntity
import kotlinx.coroutines.flow.Flow

@Suppress("Indentation")
@Dao
interface FuelStationDao {
    @Query(
        "SELECT * FROM `fuel-station` WHERE " +
            "(:fuelType = 'GASOLINE_95' AND (priceGasoline95E5 > 0 OR priceGasoline95E10 > 0)) OR " +
            "(:fuelType = 'GASOLINE_98' AND (priceGasoline98E5 > 0 OR priceGasoline98E10 > 0)) OR " +
            "(:fuelType = 'DIESEL' AND (priceGasoilA > 0 OR priceGasoilB > 0)) OR " +
            "(:fuelType = 'DIESEL_PLUS' AND priceGasoilPremium > 0)"
    )
    fun getFuelStations(fuelType: String): Flow<List<FuelStationEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertFuelStation(items: List<FuelStationEntity>)

    @Query("SELECT * FROM `fuel-station` WHERE idServiceStation = :id")
    fun getFuelStationById(id: Int): Flow<FuelStationEntity>

    @Query("UPDATE `fuel-station` SET isFavorite = :isFavorite WHERE idServiceStation = :id")
    suspend fun updateFavoriteStatus(id: Int, isFavorite: Boolean)

    @Query("SELECT * FROM `fuel-station` WHERE isFavorite = 1")
    fun getFavoriteFuelStations(): Flow<List<FuelStationEntity>>

    @Query(
        "SELECT EXISTS (SELECT 1 FROM favorite_station_cross_ref WHERE idServiceStation = :stationId AND id = :userId)"
    )
    fun isFavoriteStation(stationId: Int, userId: Long): Flow<Boolean>
}
