package com.gasguru.core.database.dao

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Transaction
import com.gasguru.core.database.model.FuelStationEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface FavoriteStationDao {

    @Query("SELECT idServiceStation FROM favorite_stations")
    fun getFavoriteStationIds(): Flow<List<Int>>

    @Query("INSERT OR IGNORE INTO favorite_stations (idServiceStation) VALUES (:stationId)")
    suspend fun addFavoriteStation(stationId: Int)

    @Query("DELETE FROM favorite_stations WHERE idServiceStation = :stationId")
    suspend fun removeFavoriteStation(stationId: Int)

    @Query("SELECT EXISTS(SELECT 1 FROM favorite_stations WHERE idServiceStation = :stationId)")
    suspend fun isFavorite(stationId: Int): Boolean

    @Transaction
    @Query(
        """
        SELECT fs.* FROM `fuel-station` fs 
        INNER JOIN favorite_stations fav ON fs.idServiceStation = fav.idServiceStation
    """
    )
    fun getFavoriteStations(): Flow<List<FuelStationEntity>>
}
