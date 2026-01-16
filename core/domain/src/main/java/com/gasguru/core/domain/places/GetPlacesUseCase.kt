package com.gasguru.core.domain.places

import com.gasguru.core.data.repository.places.PlacesRepository
import com.gasguru.core.model.data.SearchPlace
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetPlacesUseCase @Inject constructor(
    private val placesRepository: PlacesRepository
) {
    operator fun invoke(query: String): Flow<List<SearchPlace>> =
        placesRepository.getPlaces(query)
}
