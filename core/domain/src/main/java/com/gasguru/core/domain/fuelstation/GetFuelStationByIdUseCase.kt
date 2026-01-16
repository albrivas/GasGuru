package com.gasguru.core.domain.fuelstation

import com.gasguru.core.model.data.LatLng
import com.gasguru.core.data.repository.stations.OfflineFuelStationRepository
import javax.inject.Inject

class GetFuelStationByIdUseCase @Inject constructor(
    private val repository: OfflineFuelStationRepository,
) {
    operator fun invoke(id: Int, userLocation: LatLng) =
        repository.getFuelStationById(id, userLocation)
}
