package com.gasguru.core.domain.fuelstation

import com.gasguru.core.data.repository.user.UserDataRepository
import com.gasguru.core.model.data.UserWithFavoriteStations
import kotlinx.coroutines.flow.Flow

class GetFavoriteStationsWithoutDistanceUseCase(
    private val repository: UserDataRepository,
) {
    operator fun invoke(): Flow<UserWithFavoriteStations> =
        repository.getFavoriteStationsWithoutDistance()
}
