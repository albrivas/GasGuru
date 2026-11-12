package com.gasguru.core.domain.fuelstation

import com.gasguru.core.data.repository.stations.FuelStationRepository
import com.gasguru.core.model.data.AreaSearchResult
import com.gasguru.core.model.data.LatLng
import com.gasguru.core.model.data.MapBounds
import com.gasguru.core.model.data.OpeningHours
import kotlinx.coroutines.flow.Flow

class GetFuelStationsInAreaUseCase(
    private val repository: FuelStationRepository,
) {
    operator fun invoke(
        bounds: MapBounds,
        referenceLocation: LatLng,
        brands: List<String>,
        schedule: OpeningHours,
    ): Flow<AreaSearchResult> =
        repository.getFuelStationsInArea(
            bounds = bounds,
            referenceLocation = referenceLocation,
            maxStations = AREA_SEARCH_MAX_STATIONS,
            brands = brands,
            schedule = schedule,
        )

    companion object {
        const val AREA_SEARCH_MAX_STATIONS = 100
    }
}
