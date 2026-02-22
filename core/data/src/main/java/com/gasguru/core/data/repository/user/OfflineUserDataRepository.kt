package com.gasguru.core.data.repository.user

import com.gasguru.core.common.distanceTo
import com.gasguru.core.data.mapper.asEntity
import com.gasguru.core.data.mapper.calculateFuelPrices
import com.gasguru.core.data.mapper.getPriceCategory
import com.gasguru.core.database.dao.FavoriteStationDao
import com.gasguru.core.database.dao.UserDataDao
import com.gasguru.core.database.model.asExternalModel
import com.gasguru.core.model.data.FuelType
import com.gasguru.core.model.data.LatLng
import com.gasguru.core.model.data.PriceCategory
import com.gasguru.core.model.data.ThemeMode
import com.gasguru.core.model.data.UserData
import com.gasguru.core.model.data.UserWithFavoriteStations
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.map
class OfflineUserDataRepository(
    private val userDataDao: UserDataDao,
    private val favoriteStationDao: FavoriteStationDao,
) : UserDataRepository {
    override val userData: Flow<UserData>
        get() = userDataDao.getUserData()
            .map { it?.asExternalModel() ?: UserData() }

    override suspend fun updateSelectionFuel(fuelType: FuelType) {
        val user = userDataDao.getUserData().firstOrNull()?.asExternalModel() ?: UserData()
        saveUserData(user.copy(fuelSelection = fuelType, isOnboardingSuccess = true))
    }

    override suspend fun updateThemeMode(themeMode: ThemeMode) {
        val user = userDataDao.getUserData().firstOrNull()?.asExternalModel() ?: UserData()
        saveUserData(user.copy(themeMode = themeMode))
    }

    override suspend fun updateLastUpdate() {
        val user = userDataDao.getUserData().firstOrNull()?.asExternalModel() ?: UserData()
        saveUserData(user.copy(lastUpdate = System.currentTimeMillis()))
    }

    override suspend fun addFavoriteStation(stationId: Int) {
        favoriteStationDao.addFavoriteStation(stationId)
    }

    override suspend fun removeFavoriteStation(stationId: Int) {
        favoriteStationDao.removeFavoriteStation(stationId)
    }

    override fun getUserWithFavoriteStations(userLocation: LatLng): Flow<UserWithFavoriteStations> =
        combine(
            userData,
            favoriteStationDao.getFavoriteStations()
        ) { user, favoriteStationEntities ->
            val favoriteStations = favoriteStationEntities.map { it.asExternalModel() }

            val updatedStations = if (favoriteStations.size <= 1) {
                favoriteStations.map { station ->
                    station.copy(
                        distance = station.location.distanceTo(userLocation),
                        priceCategory = PriceCategory.NONE
                    )
                }
            } else {
                val (minPrice, maxPrice) = favoriteStations.calculateFuelPrices(fuelType = user.fuelSelection)
                favoriteStations.map { station ->
                    val priceCategory = station.getPriceCategory(
                        user.fuelSelection,
                        minPrice,
                        maxPrice
                    )
                    station.copy(
                        distance = station.location.distanceTo(userLocation),
                        priceCategory = priceCategory
                    )
                }
            }

            UserWithFavoriteStations(
                user = user,
                favoriteStations = updatedStations
            )
        }

    private suspend fun saveUserData(userData: UserData) {
        userDataDao.insertUserData(userData.asEntity())
    }
}
