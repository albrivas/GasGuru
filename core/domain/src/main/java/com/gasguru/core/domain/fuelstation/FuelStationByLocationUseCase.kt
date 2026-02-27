package com.gasguru.core.domain.fuelstation

import com.gasguru.core.data.repository.stations.FuelStationRepository
import com.gasguru.core.model.data.FuelStation
import com.gasguru.core.model.data.LatLng
import com.gasguru.core.model.data.OpeningHours
import kotlinx.coroutines.flow.Flow

class FuelStationByLocationUseCase(
    private val repository: FuelStationRepository,
) {
    operator fun invoke(
        userLocation: LatLng,
        maxStations: Int,
        brands: List<String>,
        schedule: OpeningHours,
    ): Flow<List<FuelStation>> =
        repository.getFuelStationByLocation(
            userLocation = userLocation,
            maxStations = maxStations,
            brands = brands,
            schedule = schedule,
        )
}
