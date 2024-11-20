package com.gasguru.core.domain

import android.location.Location
import com.gasguru.core.data.repository.OfflineFuelStationRepository
import com.gasguru.core.model.data.FuelStation
import com.gasguru.core.model.data.OpeningHours
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class FuelStationByLocationUseCase @Inject constructor(
    private val repository: OfflineFuelStationRepository,
) {
    operator fun invoke(
        userLocation: Location,
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
