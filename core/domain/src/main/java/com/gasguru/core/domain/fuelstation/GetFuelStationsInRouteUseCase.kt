package com.gasguru.core.domain.fuelstation

import com.gasguru.core.data.repository.stations.FuelStationRepository
import com.gasguru.core.model.data.FuelStation
import com.gasguru.core.model.data.LatLng

class GetFuelStationsInRouteUseCase(
    private val repository: FuelStationRepository,
) {
    suspend operator fun invoke(
        origin: LatLng,
        routePoints: List<LatLng>,
    ): List<FuelStation> = repository.getFuelStationInRoute(origin = origin, points = routePoints)
}
