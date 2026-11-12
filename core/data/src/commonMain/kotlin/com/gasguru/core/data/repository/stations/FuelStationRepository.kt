package com.gasguru.core.data.repository.stations

import com.gasguru.core.model.data.AreaSearchResult
import com.gasguru.core.model.data.FuelStation
import com.gasguru.core.model.data.LatLng
import com.gasguru.core.model.data.MapBounds
import com.gasguru.core.model.data.OpeningHours
import kotlinx.coroutines.flow.Flow

interface FuelStationRepository {
    suspend fun addAllStations()
    fun getFuelStationByLocation(
        userLocation: LatLng,
        maxStations: Int,
        brands: List<String>,
        schedule: OpeningHours,
    ): Flow<List<FuelStation>>

    fun getFuelStationsInArea(
        bounds: MapBounds,
        referenceLocation: LatLng,
        maxStations: Int,
        brands: List<String>,
        schedule: OpeningHours,
    ): Flow<AreaSearchResult>

    fun getFuelStationById(id: Int, userLocation: LatLng): Flow<FuelStation>
    suspend fun getFuelStationInRoute(origin: LatLng, points: List<LatLng>): List<FuelStation>
}
