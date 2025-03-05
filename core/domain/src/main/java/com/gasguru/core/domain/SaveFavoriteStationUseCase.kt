package com.gasguru.core.domain

import com.gasguru.core.data.repository.UserDataRepository
import javax.inject.Inject

class SaveFavoriteStationUseCase @Inject constructor(
    private val offlineRepository: UserDataRepository,
) {
    suspend operator fun invoke(stationId: Int) =
        offlineRepository.addFavoriteStation(stationId = stationId)
}
