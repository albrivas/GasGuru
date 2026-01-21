package com.gasguru.core.domain.fuelstation

import com.gasguru.core.data.repository.user.UserDataRepository
import javax.inject.Inject

class SaveFavoriteStationUseCase @Inject constructor(
    private val offlineRepository: UserDataRepository,
) {
    suspend operator fun invoke(stationId: Int): Unit =
        offlineRepository.addFavoriteStation(stationId = stationId)
}
