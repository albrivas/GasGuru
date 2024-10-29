package com.gasguru.core.domain

import android.location.Location
import com.gasguru.core.data.repository.UserDataRepository
import javax.inject.Inject

class GetFavoriteStationsUseCase @Inject constructor(
    private val repository: UserDataRepository,
) {
    operator fun invoke(userLocation: Location) =
        repository.getUserWithFavoriteStations(userLocation = userLocation)
}
