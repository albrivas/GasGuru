package com.gasguru.core.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.gasguru.core.database.model.FuelStationEntity
import kotlinx.coroutines.flow.Flow

@Suppress("Indentation")
@Dao
interface FuelStationDao {

    fun getFuelStations(fuelType: String, brands: List<String>): Flow<List<FuelStationEntity>> {
        return if (brands.isEmpty()) {
            getFuelStationsWithoutBrandFilter(fuelType)
        } else {
            getFuelStationsWithBrandFilter(fuelType, brands)
        }
    }

    @Query(
        "SELECT * FROM `fuel-station` WHERE " +
            "(" +
            "(:fuelType = 'GASOLINE_95' AND priceGasoline95E5 > 0) OR " +
            "(:fuelType = 'GASOLINE_95_PREMIUM' AND priceGasoline95E5Premium > 0) OR " +
            "(:fuelType = 'GASOLINE_95_E10' AND priceGasoline95E10 > 0) OR " +
            "(:fuelType = 'GASOLINE_98' AND priceGasoline98E5 > 0) OR " +
            "(:fuelType = 'GASOLINE_98_PREMIUM' AND priceGasoline98E10 > 0) OR " +
            "(:fuelType = 'DIESEL' AND priceGasoilA > 0) OR " +
            "(:fuelType = 'DIESEL_PLUS' AND priceGasoilPremium > 0) OR " +
            "(:fuelType = 'GASOIL_B' AND priceGasoilB > 0)" +
            ")"
    )
    fun getFuelStationsWithoutBrandFilter(fuelType: String): Flow<List<FuelStationEntity>>

    @Query(
        "SELECT * FROM `fuel-station` WHERE " +
            "(" +
            "(:fuelType = 'GASOLINE_95' AND priceGasoline95E5 > 0) OR " +
            "(:fuelType = 'GASOLINE_95_PREMIUM' AND priceGasoline95E5Premium > 0) OR " +
            "(:fuelType = 'GASOLINE_95_E10' AND priceGasoline95E10 > 0) OR " +
            "(:fuelType = 'GASOLINE_98' AND priceGasoline98E5 > 0) OR " +
            "(:fuelType = 'GASOLINE_98_PREMIUM' AND priceGasoline98E10 > 0) OR " +
            "(:fuelType = 'DIESEL' AND priceGasoilA > 0) OR " +
            "(:fuelType = 'DIESEL_PLUS' AND priceGasoilPremium > 0) OR " +
            "(:fuelType = 'GASOIL_B' AND priceGasoilB > 0)" +
            ")" +
            "AND brandStation IN (:brands) COLLATE NOCASE"
    )
    fun getFuelStationsWithBrandFilter(fuelType: String, brands: List<String>): Flow<List<FuelStationEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertFuelStation(items: List<FuelStationEntity>)

    @Query("SELECT * FROM `fuel-station` WHERE idServiceStation = :id")
    fun getFuelStationById(id: Int): Flow<FuelStationEntity>
}
