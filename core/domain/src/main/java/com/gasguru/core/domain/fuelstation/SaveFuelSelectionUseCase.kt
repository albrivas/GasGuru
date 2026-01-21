package com.gasguru.core.domain.fuelstation

import com.gasguru.core.data.repository.user.UserDataRepository
import com.gasguru.core.model.data.FuelType
import javax.inject.Inject

class SaveFuelSelectionUseCase @Inject constructor(
    private val userDataRepository: UserDataRepository,
) {
    suspend operator fun invoke(fuelType: FuelType): Unit =
        userDataRepository.updateSelectionFuel(fuelType = fuelType)
}
