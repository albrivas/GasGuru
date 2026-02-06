package com.gasguru.core.testing.fakes.data.database

import com.gasguru.core.database.dao.FuelStationDao
import com.gasguru.core.database.model.FuelStationEntity
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map

class FakeFuelStationDao(
    initialStations: List<FuelStationEntity> = emptyList(),
) : FuelStationDao {

    private val stationsFlow = MutableStateFlow(initialStations)
    private var shouldThrowError = false

    override fun getFuelStationsWithoutBrandFilter(fuelType: String): Flow<List<FuelStationEntity>> =
        if (shouldThrowError) {
            flow { throw Exception("Error getting fuel stations") }
        } else {
            stationsFlow
        }

    override fun getFuelStationsWithBrandFilter(
        fuelType: String,
        brands: List<String>,
    ): Flow<List<FuelStationEntity>> =
        if (shouldThrowError) {
            flow { throw Exception("Error getting fuel stations") }
        } else {
            stationsFlow.map { stations ->
                stations.filter { station ->
                    brands.any { brand -> station.brandStation.equals(brand, ignoreCase = true) }
                }
            }
        }

    override suspend fun insertFuelStation(items: List<FuelStationEntity>) {
        stationsFlow.value = items
    }

    override fun getFuelStationById(id: Int): Flow<FuelStationEntity> =
        stationsFlow.map { stations -> stations.first { it.idServiceStation == id } }

    override suspend fun getFuelStationsInBounds(
        minLat: Double,
        maxLat: Double,
        minLng: Double,
        maxLng: Double,
        fuelType: String,
    ): List<FuelStationEntity> =
        stationsFlow.value.filter { station ->
            station.latitude in minLat..maxLat && station.longitudeWGS84 in minLng..maxLng
        }

    fun setStations(stations: List<FuelStationEntity>) {
        stationsFlow.value = stations
    }

    fun setShouldThrowError(shouldThrow: Boolean) {
        shouldThrowError = shouldThrow
    }
}
