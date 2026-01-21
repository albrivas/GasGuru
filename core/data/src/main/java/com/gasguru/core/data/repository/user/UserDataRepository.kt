package com.gasguru.core.data.repository.user

import com.gasguru.core.model.data.FuelType
import com.gasguru.core.model.data.LatLng
import com.gasguru.core.model.data.ThemeMode
import com.gasguru.core.model.data.UserData
import com.gasguru.core.model.data.UserWithFavoriteStations
import kotlinx.coroutines.flow.Flow

interface UserDataRepository {
    val userData: Flow<UserData>
    suspend fun updateSelectionFuel(fuelType: FuelType)
    suspend fun updateThemeMode(themeMode: ThemeMode)
    suspend fun updateLastUpdate()
    suspend fun addFavoriteStation(stationId: Int)
    suspend fun removeFavoriteStation(stationId: Int)
    fun getUserWithFavoriteStations(userLocation: LatLng): Flow<UserWithFavoriteStations>
}
