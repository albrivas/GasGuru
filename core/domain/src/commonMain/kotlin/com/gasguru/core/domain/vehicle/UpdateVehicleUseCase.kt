package com.gasguru.core.domain.vehicle

import com.gasguru.core.data.repository.vehicle.VehicleRepository
import com.gasguru.core.model.data.Vehicle

class UpdateVehicleUseCase(
    private val vehicleRepository: VehicleRepository,
) {
    suspend operator fun invoke(vehicle: Vehicle) {
        vehicleRepository.upsertVehicle(vehicle = vehicle)
    }
}
