package com.gasguru.core.domain

import android.location.Location
import com.gasguru.core.data.repository.OfflineFuelStationRepository
import javax.inject.Inject

class FuelStationByLocationUseCase @Inject constructor(
    private val repository: OfflineFuelStationRepository
) {
    operator fun invoke(userLocation: Location, maxStations: Int = 12) =
        repository.getFuelStationByLocation(userLocation, maxStations)
}
