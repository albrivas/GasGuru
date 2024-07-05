package com.albrivas.fuelpump.core.domain

import com.albrivas.fuelpump.core.data.repository.PlacesRepository
import javax.inject.Inject

class GetPlacesUseCase @Inject constructor(
    private val placesRepository: PlacesRepository
) {
    operator fun invoke(query: String) = placesRepository.getPlaces(query)
}
