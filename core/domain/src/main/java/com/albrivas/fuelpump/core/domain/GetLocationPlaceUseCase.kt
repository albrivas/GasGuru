package com.albrivas.fuelpump.core.domain

import com.albrivas.fuelpump.core.data.repository.PlacesRepository
import javax.inject.Inject

class GetLocationPlaceUseCase @Inject constructor(
    private val placesRepository: PlacesRepository
) {
    operator fun invoke(placeId: String) = placesRepository.getLocationPlace(placeId)
}