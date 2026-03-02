package com.gasguru.core.testing.fakes.data.database

import com.gasguru.core.database.dao.VehicleDao
import com.gasguru.core.database.model.VehicleEntity
import com.gasguru.core.model.data.FuelType
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.update

class FakeVehicleDao(
    initialVehicles: List<VehicleEntity> = emptyList(),
) : VehicleDao {

    private val vehiclesFlow = MutableStateFlow(initialVehicles)

    override suspend fun upsertVehicle(vehicle: VehicleEntity): Long {
        val existing = vehiclesFlow.value.indexOfFirst { it.id == vehicle.id }
        if (existing >= 0) {
            vehiclesFlow.update { list -> list.toMutableList().also { it[existing] = vehicle } }
        } else {
            vehiclesFlow.update { it + vehicle }
        }
        return vehicle.id
    }

    override fun getVehiclesByUser(userId: Long): Flow<List<VehicleEntity>> =
        vehiclesFlow.map { list -> list.filter { it.userId == userId } }

    override suspend fun getVehicleById(vehicleId: Long): VehicleEntity? =
        vehiclesFlow.value.firstOrNull { it.id == vehicleId }

    override suspend fun updateTankCapacity(vehicleId: Long, tankCapacity: Int) {
        vehiclesFlow.update { list ->
            list.map { if (it.id == vehicleId) it.copy(tankCapacity = tankCapacity) else it }
        }
    }

    override suspend fun updateFuelType(vehicleId: Long, fuelType: FuelType) {
        vehiclesFlow.update { list ->
            list.map { if (it.id == vehicleId) it.copy(fuelType = fuelType) else it }
        }
    }
}
