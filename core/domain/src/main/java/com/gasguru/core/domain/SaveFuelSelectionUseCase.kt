package com.gasguru.core.domain

import com.gasguru.core.data.repository.UserDataRepository
import javax.inject.Inject

class SaveFuelSelectionUseCase @Inject constructor(
    private val userDataRepository: UserDataRepository,
) {
    suspend operator fun invoke(fuelType: String) =
        userDataRepository.updateSelectionFuel(fuelType = fuelType)
}
