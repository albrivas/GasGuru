package com.gasguru.core.domain.fuelstation

import com.gasguru.core.data.repository.user.UserDataRepository
import com.gasguru.core.model.data.FuelType

class SaveFuelSelectionUseCase(
    private val userDataRepository: UserDataRepository,
) {
    suspend operator fun invoke(fuelType: FuelType): Unit =
        userDataRepository.updateSelectionFuel(fuelType = fuelType)
}
