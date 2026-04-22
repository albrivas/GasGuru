package com.gasguru.core.domain.vehicle

import com.gasguru.core.data.repository.vehicle.VehicleRepository

class DeleteVehicleUseCase(
    private val vehicleRepository: VehicleRepository,
) {
    suspend operator fun invoke(vehicleId: Long) {
        vehicleRepository.deleteVehicle(vehicleId = vehicleId)
    }
}
