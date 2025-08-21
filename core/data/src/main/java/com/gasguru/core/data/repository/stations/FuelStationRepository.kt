package com.gasguru.core.data.repository.stations

import android.location.Location
import com.gasguru.core.model.data.FuelStation
import com.gasguru.core.model.data.LatLng
import com.gasguru.core.model.data.OpeningHours
import kotlinx.coroutines.flow.Flow

interface FuelStationRepository {
    suspend fun addAllStations()
    fun getFuelStationByLocation(
        userLocation: Location,
        maxStations: Int,
        brands: List<String>,
        schedule: OpeningHours,
    ): Flow<List<FuelStation>>

    fun getFuelStationById(id: Int, userLocation: Location): Flow<FuelStation>
    suspend fun getFuelStationInRoute(points: List<LatLng>): List<FuelStation>
}
