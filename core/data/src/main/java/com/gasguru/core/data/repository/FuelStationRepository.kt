package com.gasguru.core.data.repository

import android.location.Location
import com.gasguru.core.model.data.FuelStation
import kotlinx.coroutines.flow.Flow

interface FuelStationRepository {
    suspend fun addAllStations()
    fun getFuelStationByLocation(userLocation: Location, maxStations: Int): Flow<List<FuelStation>>
    fun getFuelStationById(id: Int, userLocation: Location): Flow<FuelStation>
    suspend fun updateFavoriteStatus(id: Int, isFavorite: Boolean)
    fun getFavoriteFuelStations(userLocation: Location): Flow<List<FuelStation>>
}
