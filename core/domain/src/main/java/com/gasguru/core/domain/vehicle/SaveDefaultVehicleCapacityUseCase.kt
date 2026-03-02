package com.gasguru.core.domain.vehicle

import com.gasguru.core.data.repository.user.UserDataRepository
import com.gasguru.core.data.repository.vehicle.VehicleRepository
import kotlinx.coroutines.flow.first

class SaveDefaultVehicleCapacityUseCase(
    private val vehicleRepository: VehicleRepository,
    private val userDataRepository: UserDataRepository,
) {
    suspend operator fun invoke(tankCapacity: Int) {
        val vehicle = userDataRepository.userData.first().vehicles.first()
        vehicleRepository.updateTankCapacity(vehicleId = vehicle.id, tankCapacity = tankCapacity)
        userDataRepository.setOnboardingComplete()
    }
}
