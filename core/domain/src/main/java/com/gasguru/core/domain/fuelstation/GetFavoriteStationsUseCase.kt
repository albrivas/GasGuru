package com.gasguru.core.domain.fuelstation

import com.gasguru.core.data.repository.user.UserDataRepository
import com.gasguru.core.model.data.LatLng
import com.gasguru.core.model.data.UserWithFavoriteStations
import kotlinx.coroutines.flow.Flow

class GetFavoriteStationsUseCase(
    private val repository: UserDataRepository,
) {
    operator fun invoke(userLocation: LatLng): Flow<UserWithFavoriteStations> =
        repository.getUserWithFavoriteStations(userLocation = userLocation)
}
