package com.gasguru.core.data.repository.vehicle

import com.gasguru.core.data.mapper.asEntity
import com.gasguru.core.database.dao.VehicleDao
import com.gasguru.core.database.model.asExternalModel
import com.gasguru.core.model.data.FuelType
import com.gasguru.core.model.data.Vehicle
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class OfflineVehicleRepository(
    private val vehicleDao: VehicleDao,
) : VehicleRepository {

    override fun getVehiclesForUser(userId: Long): Flow<List<Vehicle>> =
        vehicleDao.getVehiclesByUser(userId = userId)
            .map { entities -> entities.map { it.asExternalModel() } }

    override suspend fun upsertVehicle(vehicle: Vehicle): Long =
        vehicleDao.upsertVehicle(vehicle = vehicle.asEntity())

    override suspend fun updateTankCapacity(vehicleId: Long, tankCapacity: Int) {
        vehicleDao.updateTankCapacity(vehicleId = vehicleId, tankCapacity = tankCapacity)
    }

    override suspend fun updateFuelType(vehicleId: Long, fuelType: FuelType) {
        vehicleDao.updateFuelType(vehicleId = vehicleId, fuelType = fuelType)
    }

    override suspend fun getVehicleById(vehicleId: Long): Vehicle? =
        vehicleDao.getVehicleById(vehicleId = vehicleId)?.asExternalModel()
}