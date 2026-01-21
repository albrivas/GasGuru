package com.gasguru.core.testing.fakes.data.places

import com.gasguru.core.data.repository.places.PlacesRepository
import com.gasguru.core.model.data.LatLng
import com.gasguru.core.model.data.SearchPlace
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf

class FakePlacesRepository(
    initialPlaces: Map<String, List<SearchPlace>> = emptyMap(),
    initialLocations: Map<String, LatLng> = emptyMap(),
) : PlacesRepository {

    private val placesByQuery = initialPlaces.toMutableMap()
    private val locationsById = initialLocations.toMutableMap()

    val requestedQueries = mutableListOf<String>()
    val requestedLocationIds = mutableListOf<String>()

    override fun getPlaces(query: String): Flow<List<SearchPlace>> {
        requestedQueries.add(query)
        return flowOf(placesByQuery[query].orEmpty())
    }

    override fun getLocationPlace(placeId: String): Flow<LatLng> {
        requestedLocationIds.add(placeId)
        return flowOf(locationsById[placeId] ?: LatLng(0.0, 0.0))
    }

    fun setPlacesForQuery(query: String, places: List<SearchPlace>) {
        placesByQuery[query] = places
    }

    fun setLocationForId(placeId: String, location: LatLng) {
        locationsById[placeId] = location
    }
}
