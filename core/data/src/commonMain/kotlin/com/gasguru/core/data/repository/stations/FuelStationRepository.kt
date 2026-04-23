package com.gasguru.core.data.repository.stations

import com.gasguru.core.model.data.FuelStation
import com.gasguru.core.model.data.LatLng
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

    fun getFuelStationById(id: Int, userLocation: LatLng): Flow<FuelStation>
    suspend fun getFuelStationInRoute(origin: LatLng, points: List<LatLng>): List<FuelStation>
}
