package com.gasguru.core.data.repository

import android.location.Location
import com.gasguru.core.model.data.UserData
import com.gasguru.core.model.data.UserWithFavoriteStations
import kotlinx.coroutines.flow.Flow

interface UserDataRepository {
    val userData: Flow<UserData>
    suspend fun updateUserData(userData: UserData)
    suspend fun addFavoriteStation(stationId: Int)
    suspend fun removeFavoriteStation(stationId: Int)
    fun getUserWithFavoriteStations(userLocation: Location): Flow<UserWithFavoriteStations>
}
