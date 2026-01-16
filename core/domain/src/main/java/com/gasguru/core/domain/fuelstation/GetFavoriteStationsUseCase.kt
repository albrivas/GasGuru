package com.gasguru.core.domain.fuelstation

import com.gasguru.core.model.data.LatLng
import com.gasguru.core.data.repository.user.UserDataRepository
import javax.inject.Inject

class GetFavoriteStationsUseCase @Inject constructor(
    private val repository: UserDataRepository,
) {
    operator fun invoke(userLocation: LatLng) =
        repository.getUserWithFavoriteStations(userLocation = userLocation)
}
