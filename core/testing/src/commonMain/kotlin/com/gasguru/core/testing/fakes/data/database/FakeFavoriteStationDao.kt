package com.gasguru.core.testing.fakes.data.database

import com.gasguru.core.database.dao.FavoriteStationDao
import com.gasguru.core.database.model.FuelStationEntity
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow

class FakeFavoriteStationDao(
    initialFavoriteIds: List<Int> = emptyList(),
    initialFavoriteStations: List<FuelStationEntity> = emptyList(),
) : FavoriteStationDao {

    private val favoriteIdsFlow = MutableStateFlow(initialFavoriteIds)
    private val favoriteStationsFlow = MutableStateFlow(initialFavoriteStations)

    override fun getFavoriteStationIds(): Flow<List<Int>> = favoriteIdsFlow

    override suspend fun addFavoriteStation(stationId: Int) {
        favoriteIdsFlow.value = (favoriteIdsFlow.value + stationId).distinct()
    }

    override suspend fun removeFavoriteStation(stationId: Int) {
        favoriteIdsFlow.value = favoriteIdsFlow.value.filterNot { it == stationId }
    }

    override suspend fun isFavorite(stationId: Int): Boolean =
        favoriteIdsFlow.value.contains(stationId)

    override fun getFavoriteStations(): Flow<List<FuelStationEntity>> = favoriteStationsFlow

    fun setFavoriteStations(stations: List<FuelStationEntity>) {
        favoriteStationsFlow.value = stations
    }
}
