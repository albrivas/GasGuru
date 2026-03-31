package com.gasguru.core.domain.places

import com.gasguru.core.data.repository.places.PlacesRepository
import com.gasguru.core.model.data.SearchPlace
import kotlinx.coroutines.flow.Flow

class GetPlacesUseCase(
    private val placesRepository: PlacesRepository
) {
    operator fun invoke(query: String): Flow<List<SearchPlace>> =
        placesRepository.getPlaces(query)
}
