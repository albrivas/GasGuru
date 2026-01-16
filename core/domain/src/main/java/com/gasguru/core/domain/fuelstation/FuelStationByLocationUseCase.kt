package com.gasguru.core.domain.fuelstation

import com.gasguru.core.model.data.LatLng
import com.gasguru.core.data.repository.stations.OfflineFuelStationRepository
import com.gasguru.core.model.data.FuelStation
import com.gasguru.core.model.data.OpeningHours
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class FuelStationByLocationUseCase @Inject constructor(
    private val repository: OfflineFuelStationRepository,
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
