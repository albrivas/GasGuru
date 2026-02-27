package com.gasguru.core.domain.vehicle

import com.gasguru.core.data.repository.vehicle.VehicleRepository
import com.gasguru.core.model.data.FuelType

class UpdateVehicleFuelTypeUseCase(
    private val vehicleRepository: VehicleRepository,
) {
    suspend operator fun invoke(vehicleId: Long, fuelType: FuelType) {
        vehicleRepository.updateFuelType(vehicleId = vehicleId, fuelType = fuelType)
    }
}