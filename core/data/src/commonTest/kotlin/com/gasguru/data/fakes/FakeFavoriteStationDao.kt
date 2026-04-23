package com.gasguru.data.fakes

import com.gasguru.core.database.dao.FavoriteStationDao
import com.gasguru.core.database.model.FuelStationEntity
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.update

class FakeFavoriteStationDao(
    initialStations: List<FuelStationEntity> = emptyList(),
) : FavoriteStationDao {

    private val stationsFlow = MutableStateFlow(initialStations)

    override fun getFavoriteStationIds(): Flow<List<Int>> =
        stationsFlow.map { stations -> stations.map { it.idServiceStation } }

    override suspend fun addFavoriteStation(stationId: Int) {
        // intentionally empty for tests that only observe state
    }

    override suspend fun removeFavoriteStation(stationId: Int) {
        stationsFlow.update { stations -> stations.filterNot { it.idServiceStation == stationId } }
    }

    override suspend fun isFavorite(stationId: Int): Boolean =
        stationsFlow.value.any { it.idServiceStation == stationId }

    override fun getFavoriteStations(): Flow<List<FuelStationEntity>> = stationsFlow

    fun setStations(stations: List<FuelStationEntity>) {
        stationsFlow.value = stations
    }
}
