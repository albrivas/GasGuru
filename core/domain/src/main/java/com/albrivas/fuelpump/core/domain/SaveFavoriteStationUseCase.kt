package com.albrivas.fuelpump.core.domain

import com.albrivas.fuelpump.core.data.repository.FuelStationRepository
import javax.inject.Inject

class SaveFavoriteStationUseCase @Inject constructor(
    private val offlineRepository: FuelStationRepository,
) {
    suspend operator fun invoke(idStation: Int, isFavorite: Boolean) =
        offlineRepository.updateFavoriteStatus(idStation, isFavorite)
}
