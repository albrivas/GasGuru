package com.gasguru.core.data.repository.places

import com.gasguru.core.model.data.SearchPlace
import com.gasguru.core.model.data.LatLng
import kotlinx.coroutines.flow.Flow

interface PlacesRepository {
    fun getPlaces(query: String): Flow<List<SearchPlace>>
    fun getLocationPlace(placeId: String): Flow<LatLng>
}
