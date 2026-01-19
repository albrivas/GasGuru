package com.gasguru.core.testing.fakes.data.user

import com.gasguru.core.data.repository.user.UserDataRepository
import com.gasguru.core.model.data.FuelStation
import com.gasguru.core.model.data.FuelType
import com.gasguru.core.model.data.LatLng
import com.gasguru.core.model.data.ThemeMode
import com.gasguru.core.model.data.UserData
import com.gasguru.core.model.data.UserWithFavoriteStations
import com.gasguru.core.model.data.previewFuelStationDomain
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.update

class FakeUserDataRepository(
    initialUserData: UserData = UserData(),
    initialFavoriteStations: List<FuelStation> = emptyList(),
) : UserDataRepository {

    private val userDataFlow = MutableStateFlow(initialUserData)
    private val favoriteStationsFlow = MutableStateFlow(initialFavoriteStations)

    val removedFavoriteStations = mutableListOf<Int>()
    val addedFavoriteStations = mutableListOf<Int>()
    val updatedFuelSelections = mutableListOf<FuelType>()
    val updatedThemeModes = mutableListOf<ThemeMode>()

    override val userData: Flow<UserData> = userDataFlow

    override suspend fun updateSelectionFuel(fuelType: FuelType) {
        updatedFuelSelections.add(fuelType)
        userDataFlow.update { it.copy(fuelSelection = fuelType) }
    }

    override suspend fun updateThemeMode(themeMode: ThemeMode) {
        updatedThemeModes.add(themeMode)
        userDataFlow.update { it.copy(themeMode = themeMode) }
    }

    override suspend fun updateLastUpdate() {
        userDataFlow.update { it.copy(lastUpdate = System.currentTimeMillis()) }
    }

    override suspend fun addFavoriteStation(stationId: Int) {
        addedFavoriteStations.add(stationId)
        favoriteStationsFlow.update { current ->
            current + previewFuelStationDomain(stationId).copy(isFavorite = true)
        }
    }

    override suspend fun removeFavoriteStation(stationId: Int) {
        removedFavoriteStations.add(stationId)
        favoriteStationsFlow.update { stations ->
            stations.filterNot { it.idServiceStation == stationId }
        }
    }

    override fun getUserWithFavoriteStations(userLocation: LatLng): Flow<UserWithFavoriteStations> =
        combine(userDataFlow, favoriteStationsFlow) { userData, stations ->
            UserWithFavoriteStations(user = userData, favoriteStations = stations)
        }

    fun setUserData(userData: UserData) {
        userDataFlow.value = userData
    }

    fun setFavoriteStations(stations: List<FuelStation>) {
        favoriteStationsFlow.value = stations
    }
}
