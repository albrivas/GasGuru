package com.gasguru.core.domain.fuelstation

import com.gasguru.core.data.repository.user.UserDataRepository

class RemoveFavoriteStationUseCase(
    private val offlineRepository: UserDataRepository,
) {
    suspend operator fun invoke(stationId: Int): Unit =
        offlineRepository.removeFavoriteStation(stationId = stationId)
}
