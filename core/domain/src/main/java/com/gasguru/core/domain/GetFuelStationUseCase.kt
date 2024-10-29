package com.gasguru.core.domain

import com.gasguru.core.data.repository.OfflineFuelStationRepository
import javax.inject.Inject

class GetFuelStationUseCase @Inject constructor(
    private val repository: OfflineFuelStationRepository
) {
    suspend fun getFuelInAllStations() {
        repository.addAllStations()
    }
}
