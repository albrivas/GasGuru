package com.gasguru.core.domain.fuelstation

import com.gasguru.core.data.repository.stations.FuelStationRepository

class GetFuelStationUseCase(
    private val repository: FuelStationRepository,
) {
    suspend fun getFuelInAllStations() {
        repository.addAllStations()
    }
}
