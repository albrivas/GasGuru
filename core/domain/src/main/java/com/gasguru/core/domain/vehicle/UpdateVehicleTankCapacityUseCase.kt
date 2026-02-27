package com.gasguru.core.domain.vehicle

import com.gasguru.core.data.repository.vehicle.VehicleRepository

class UpdateVehicleTankCapacityUseCase(
    private val vehicleRepository: VehicleRepository,
) {
    suspend operator fun invoke(vehicleId: Long, tankCapacity: Int) {
        vehicleRepository.updateTankCapacity(vehicleId = vehicleId, tankCapacity = tankCapacity)
    }
}