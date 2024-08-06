package com.albrivas.fuelpump.core.domain

import com.albrivas.fuelpump.core.data.repository.OfflineFuelStationRepository
import javax.inject.Inject

class GetFavoriteStationsUseCase @Inject constructor(
    private val repository: OfflineFuelStationRepository,
) {
    operator fun invoke() =
        repository.getFavoriteFuelStations()
}
