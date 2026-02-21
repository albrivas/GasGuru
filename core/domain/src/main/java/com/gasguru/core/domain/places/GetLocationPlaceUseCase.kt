package com.gasguru.core.domain.places

import com.gasguru.core.data.repository.places.PlacesRepository
import com.gasguru.core.model.data.LatLng
import kotlinx.coroutines.flow.Flow

class GetLocationPlaceUseCase(
    private val placesRepository: PlacesRepository
) {
    operator fun invoke(placeId: String): Flow<LatLng> =
        placesRepository.getLocationPlace(placeId)
}
