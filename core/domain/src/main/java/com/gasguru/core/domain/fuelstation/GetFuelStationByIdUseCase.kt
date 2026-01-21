package com.gasguru.core.domain.fuelstation

import com.gasguru.core.data.repository.stations.OfflineFuelStationRepository
import com.gasguru.core.model.data.FuelStation
import com.gasguru.core.model.data.LatLng
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetFuelStationByIdUseCase @Inject constructor(
    private val repository: OfflineFuelStationRepository,
) {
    operator fun invoke(id: Int, userLocation: LatLng): Flow<FuelStation> =
        repository.getFuelStationById(id, userLocation)
}
