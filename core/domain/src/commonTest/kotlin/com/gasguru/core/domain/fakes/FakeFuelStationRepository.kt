package com.gasguru.core.domain.fakes

import com.gasguru.core.data.repository.stations.FuelStationRepository
import com.gasguru.core.model.data.FuelStation
import com.gasguru.core.model.data.LatLng
import com.gasguru.core.model.data.OpeningHours
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOf

class FakeFuelStationRepository : FuelStationRepository {

    private val stationsFlow = MutableStateFlow<List<FuelStation>>(emptyList())

    var addAllStationsCalls = 0
        private set

    val locationRequests = mutableListOf<Triple<LatLng, Int, OpeningHours>>()
    val byIdRequests = mutableListOf<Pair<Int, LatLng>>()
    val routeRequests = mutableListOf<Pair<LatLng, List<LatLng>>>()

    private var stationById: FuelStation? = null
    private var routeStations: List<FuelStation> = emptyList()

    override suspend fun addAllStations() {
        addAllStationsCalls++
    }

    override fun getFuelStationByLocation(
        userLocation: LatLng,
        maxStations: Int,
        brands: List<String>,
        schedule: OpeningHours,
    ): Flow<List<FuelStation>> {
        locationRequests.add(Triple(userLocation, maxStations, schedule))
        return stationsFlow
    }

    override fun getFuelStationById(id: Int, userLocation: LatLng): Flow<FuelStation> {
        byIdRequests.add(id to userLocation)
        return stationById?.let { flowOf(it) }
            ?: flow { throw NoSuchElementException("Station $id not found") }
    }

    override suspend fun getFuelStationInRoute(origin: LatLng, points: List<LatLng>): List<FuelStation> {
        routeRequests.add(origin to points)
        return routeStations
    }

    fun setStations(stations: List<FuelStation>) {
        stationsFlow.value = stations
    }

    fun setStationById(station: FuelStation) {
        stationById = station
    }

    fun setRouteStations(stations: List<FuelStation>) {
        routeStations = stations
    }
}
