package com.gasguru.core.domain.vehicle

import com.gasguru.core.data.repository.vehicle.VehicleRepository
import com.gasguru.core.model.data.FuelType
import com.gasguru.core.model.data.Vehicle

class SaveDefaultVehicleFuelTypeUseCase(
    private val vehicleRepository: VehicleRepository,
) {
    suspend operator fun invoke(fuelType: FuelType) {
        vehicleRepository.upsertVehicle(
            vehicle = Vehicle(
                userId = 0L,
                name = null,
                fuelType = fuelType,
                tankCapacity = 40,
            ),
        )
    }
}
