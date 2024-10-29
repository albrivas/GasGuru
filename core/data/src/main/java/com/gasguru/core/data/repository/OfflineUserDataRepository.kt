package com.gasguru.core.data.repository

import android.location.Location
import com.gasguru.core.data.mapper.asEntity
import com.gasguru.core.database.dao.UserDataDao
import com.gasguru.core.database.model.FavoriteStationCrossRef
import com.gasguru.core.database.model.asExternalModel
import com.gasguru.core.model.data.UserData
import com.gasguru.core.model.data.UserWithFavoriteStations
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class OfflineUserDataRepository @Inject constructor(
    private val userDataDao: UserDataDao,
) : UserDataRepository {
    override val userData: Flow<UserData>
        get() = userDataDao.getUserData().map { it.asExternalModel() }

    override suspend fun updateUserData(userData: UserData) =
        userDataDao.insertUserData(userData.asEntity())

    override suspend fun addFavoriteStation(stationId: Int) {
        val userId = getUserId()
        val crossRef = FavoriteStationCrossRef(id = userId, idServiceStation = stationId)
        userDataDao.insertFavoriteStationCrossRef(crossRef)
    }

    override suspend fun removeFavoriteStation(stationId: Int) {
        val userId = getUserId()
        val crossRef = FavoriteStationCrossRef(id = userId, idServiceStation = stationId)
        userDataDao.deleteFavoriteStationCrossRef(crossRef)
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    override fun getUserWithFavoriteStations(userLocation: Location): Flow<UserWithFavoriteStations> =
        userDataDao.getUserData().flatMapLatest { userEntity ->
            userDataDao.getUserWithFavoriteStations(userEntity.id)
                .map { userWithFavorites ->
                    val updatedStations = userWithFavorites.asExternalModel().favoriteStations.map { station ->
                        station.copy(distance = station.location.distanceTo(userLocation))
                    }
                    UserWithFavoriteStations(
                        user = userWithFavorites.asExternalModel().user,
                        favoriteStations = updatedStations
                    )
                }
        }

    private suspend fun getUserId(): Long = userDataDao.getUserId()
}
