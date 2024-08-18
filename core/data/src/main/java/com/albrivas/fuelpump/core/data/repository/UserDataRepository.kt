package com.albrivas.fuelpump.core.data.repository

import com.albrivas.fuelpump.core.model.data.UserData
import com.albrivas.fuelpump.core.model.data.UserWithFavoriteStations
import kotlinx.coroutines.flow.Flow

interface UserDataRepository {
    val userData: Flow<UserData>
    suspend fun updateUserData(userData: UserData)
    suspend fun addFavoriteStation(stationId: Int)
    suspend fun removeFavoriteStation(stationId: Int)
    fun getUserWithFavoriteStations(): Flow<UserWithFavoriteStations>
}
