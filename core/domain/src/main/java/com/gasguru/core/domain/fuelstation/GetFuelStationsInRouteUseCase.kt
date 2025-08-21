package com.gasguru.core.domain.fuelstation

import com.gasguru.core.data.repository.stations.OfflineFuelStationRepository
import com.gasguru.core.model.data.FuelStation
import com.gasguru.core.model.data.LatLng
import javax.inject.Inject

class GetFuelStationsInRouteUseCase @Inject constructor(
    private val repository: OfflineFuelStationRepository,
) {
    suspend operator fun invoke(
        routePoints: List<LatLng>
    ): List<FuelStation> = repository.getFuelStationInRoute(routePoints)
}
