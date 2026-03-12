package com.gasguru.core.domain.vehicle

import com.gasguru.core.data.repository.vehicle.VehicleRepository
import com.gasguru.core.model.data.Vehicle

class GetVehicleByIdUseCase(
    private val vehicleRepository: VehicleRepository,
) {
    suspend operator fun invoke(vehicleId: Long): Vehicle? =
        vehicleRepository.getVehicleById(vehicleId = vehicleId)
}
