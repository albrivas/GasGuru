package com.gasguru.core.domain

import com.gasguru.core.data.repository.places.PlacesRepository
import javax.inject.Inject

class GetPlacesUseCase @Inject constructor(
    private val placesRepository: PlacesRepository
) {
    operator fun invoke(query: String) = placesRepository.getPlaces(query)
}
