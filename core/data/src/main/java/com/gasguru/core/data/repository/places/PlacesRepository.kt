package com.gasguru.core.data.repository.places

import android.location.Location
import com.gasguru.core.model.data.SearchPlace
import kotlinx.coroutines.flow.Flow

interface PlacesRepository {
    fun getPlaces(query: String): Flow<List<SearchPlace>>
    fun getLocationPlace(placeId: String): Flow<Location>
}
