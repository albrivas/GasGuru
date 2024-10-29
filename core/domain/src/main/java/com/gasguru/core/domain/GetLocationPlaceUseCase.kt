package com.gasguru.core.domain

import com.gasguru.core.data.repository.PlacesRepository
import javax.inject.Inject

class GetLocationPlaceUseCase @Inject constructor(
    private val placesRepository: PlacesRepository
) {
    operator fun invoke(placeId: String) = placesRepository.getLocationPlace(placeId)
}
