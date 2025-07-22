package com.gasguru.core.domain.places

import com.gasguru.core.data.repository.places.PlacesRepository
import javax.inject.Inject

class GetLocationPlaceUseCase @Inject constructor(
    private val placesRepository: PlacesRepository
) {
    operator fun invoke(placeId: String) = placesRepository.getLocationPlace(placeId)
}
