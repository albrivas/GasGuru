package com.gasguru.core.domain.fuelstation

import com.gasguru.core.data.repository.stations.FuelStationRepository
import com.gasguru.core.model.data.AreaSearchResult
import com.gasguru.core.model.data.MapBounds
import com.gasguru.core.model.data.OpeningHours
import kotlinx.coroutines.flow.Flow

class GetFuelStationsInAreaUseCase(
    private val repository: FuelStationRepository,
) {
    operator fun invoke(
        bounds: MapBounds,
        maxStations: Int,
        brands: List<String>,
        schedule: OpeningHours,
    ): Flow<AreaSearchResult> =
        repository.getFuelStationsInArea(
            bounds = bounds,
            maxStations = maxStations,
            brands = brands,
            schedule = schedule,
        )
}
