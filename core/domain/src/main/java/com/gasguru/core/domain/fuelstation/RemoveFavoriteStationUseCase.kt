package com.gasguru.core.domain.fuelstation

import com.gasguru.core.data.repository.user.UserDataRepository
import javax.inject.Inject

class RemoveFavoriteStationUseCase @Inject constructor(
    private val offlineRepository: UserDataRepository,
) {
    suspend operator fun invoke(stationId: Int) =
        offlineRepository.removeFavoriteStation(stationId = stationId)
}
