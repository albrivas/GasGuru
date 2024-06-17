package com.albrivas.fuelpump.core.data.repository

import android.location.Location
import com.albrivas.fuelpump.core.model.data.SearchPlace
import kotlinx.coroutines.flow.Flow

interface PlacesRepository {
    fun getPlaces(query: String): Flow<List<SearchPlace>>
    fun getLocationPlace(placeId: String): Flow<Location>
}