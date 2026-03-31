package com.gasguru.core.domain.vehicle

import com.gasguru.core.data.repository.vehicle.VehicleRepository
import com.gasguru.core.model.data.Vehicle
import kotlinx.coroutines.flow.Flow

class GetVehiclesUseCase(
    private val vehicleRepository: VehicleRepository,
) {
    operator fun invoke(userId: Long): Flow<List<Vehicle>> =
        vehicleRepository.getVehiclesForUser(userId = userId)
}
