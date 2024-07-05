package com.albrivas.fuelpump.core.domain

import com.albrivas.fuelpump.core.data.repository.OfflineFuelStationRepository
import javax.inject.Inject

class GetFuelStationUseCase @Inject constructor(
    private val repository: OfflineFuelStationRepository
) {
    suspend fun getFuelInAllStations() {
        repository.addAllStations()
    }
}
