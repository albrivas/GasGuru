package com.gasguru.core.domain.fuelstation

import com.gasguru.core.data.repository.stations.FuelStationRepository
import com.gasguru.core.model.data.FuelStation
import com.gasguru.core.model.data.LatLng
import kotlinx.coroutines.flow.Flow

class GetFuelStationByIdUseCase(
    private val repository: FuelStationRepository,
) {
    operator fun invoke(id: Int, userLocation: LatLng): Flow<FuelStation> =
        repository.getFuelStationById(id, userLocation)
}
