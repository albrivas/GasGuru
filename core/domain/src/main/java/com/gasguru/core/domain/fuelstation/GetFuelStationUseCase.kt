package com.gasguru.core.domain.fuelstation

import com.gasguru.core.data.repository.stations.OfflineFuelStationRepository
import javax.inject.Inject

class GetFuelStationUseCase @Inject constructor(
    private val repository: OfflineFuelStationRepository
) {
    suspend fun getFuelInAllStations() {
        repository.addAllStations()
    }
}
