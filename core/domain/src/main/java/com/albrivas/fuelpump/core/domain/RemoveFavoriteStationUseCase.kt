package com.albrivas.fuelpump.core.domain

import com.albrivas.fuelpump.core.data.repository.UserDataRepository
import javax.inject.Inject

class RemoveFavoriteStationUseCase @Inject constructor(
    private val offlineRepository: UserDataRepository,
) {
    suspend operator fun invoke(stationId: Int) =
        offlineRepository.removeFavoriteStation(stationId = stationId)
}
