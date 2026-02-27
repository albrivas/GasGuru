package com.gasguru.core.testing.fakes.data.vehicle

import com.gasguru.core.data.repository.vehicle.VehicleRepository
import com.gasguru.core.model.data.FuelType
import com.gasguru.core.model.data.Vehicle
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.update

class FakeVehicleRepository(
    initialVehicles: List<Vehicle> = emptyList(),
) : VehicleRepository {

    private val vehiclesFlow = MutableStateFlow(initialVehicles)

    val updatedFuelTypes = mutableListOf<Pair<Long, FuelType>>()
    val updatedTankCapacities = mutableListOf<Pair<Long, Int>>()

    override fun getVehiclesForUser(userId: Long): Flow<List<Vehicle>> =
        vehiclesFlow.map { list -> list.filter { it.userId == userId } }

    override suspend fun upsertVehicle(vehicle: Vehicle): Long {
        vehiclesFlow.update { list ->
            val existing = list.indexOfFirst { it.id == vehicle.id }
            if (existing >= 0) {
                list.toMutableList().also { it[existing] = vehicle }
            } else {
                list + vehicle
            }
        }
        return vehicle.id
    }

    override suspend fun updateTankCapacity(vehicleId: Long, tankCapacity: Int) {
        updatedTankCapacities.add(vehicleId to tankCapacity)
        vehiclesFlow.update { list ->
            list.map { if (it.id == vehicleId) it.copy(tankCapacity = tankCapacity) else it }
        }
    }

    override suspend fun updateFuelType(vehicleId: Long, fuelType: FuelType) {
        updatedFuelTypes.add(vehicleId to fuelType)
        vehiclesFlow.update { list ->
            list.map { if (it.id == vehicleId) it.copy(fuelType = fuelType) else it }
        }
    }

    override suspend fun getVehicleById(vehicleId: Long): Vehicle? =
        vehiclesFlow.value.firstOrNull { it.id == vehicleId }
}