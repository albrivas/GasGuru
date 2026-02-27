package com.gasguru.core.data.repository.vehicle

import com.gasguru.core.model.data.FuelType
import com.gasguru.core.model.data.Vehicle
import kotlinx.coroutines.flow.Flow

interface VehicleRepository {
    fun getVehiclesForUser(userId: Long): Flow<List<Vehicle>>
    suspend fun upsertVehicle(vehicle: Vehicle): Long
    suspend fun updateTankCapacity(vehicleId: Long, tankCapacity: Int)
    suspend fun updateFuelType(vehicleId: Long, fuelType: FuelType)
    suspend fun getVehicleById(vehicleId: Long): Vehicle?
}