package com.gasguru.core.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.gasguru.core.database.model.VehicleEntity
import com.gasguru.core.model.data.FuelType
import kotlinx.coroutines.flow.Flow

@Dao
interface VehicleDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertVehicle(vehicle: VehicleEntity): Long

    @Query("SELECT * FROM vehicles WHERE userId = :userId")
    fun getVehiclesByUser(userId: Long): Flow<List<VehicleEntity>>

    @Query("SELECT * FROM vehicles WHERE id = :vehicleId")
    suspend fun getVehicleById(vehicleId: Long): VehicleEntity?

    @Query("UPDATE vehicles SET tankCapacity = :tankCapacity WHERE id = :vehicleId")
    suspend fun updateTankCapacity(vehicleId: Long, tankCapacity: Int)

    @Query("UPDATE vehicles SET fuelType = :fuelType WHERE id = :vehicleId")
    suspend fun updateFuelType(vehicleId: Long, fuelType: FuelType)
}
