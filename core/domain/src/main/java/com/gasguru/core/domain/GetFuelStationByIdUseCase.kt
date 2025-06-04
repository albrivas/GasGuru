package com.gasguru.core.domain

import android.location.Location
import com.gasguru.core.data.repository.stations.OfflineFuelStationRepository
import javax.inject.Inject

class GetFuelStationByIdUseCase @Inject constructor(
    private val repository: OfflineFuelStationRepository,
) {
    operator fun invoke(id: Int, userLocation: Location) =
        repository.getFuelStationById(id, userLocation)
}
