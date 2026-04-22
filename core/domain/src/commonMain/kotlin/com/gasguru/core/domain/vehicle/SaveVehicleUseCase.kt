package com.gasguru.core.domain.vehicle

import com.gasguru.core.data.repository.vehicle.VehicleRepository
import com.gasguru.core.model.data.Vehicle

class SaveVehicleUseCase(
    private val vehicleRepository: VehicleRepository,
) {
    suspend operator fun invoke(vehicle: Vehicle) {
        if (vehicle.isPrincipal) {
            vehicleRepository.clearPrincipalVehiclesForUser(userId = vehicle.userId)
        }
        vehicleRepository.upsertVehicle(vehicle = vehicle)
    }
}
