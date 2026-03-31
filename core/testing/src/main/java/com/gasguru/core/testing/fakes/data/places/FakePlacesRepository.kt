package com.gasguru.core.testing.fakes.data.places

import com.gasguru.core.data.repository.places.PlacesRepository
import com.gasguru.core.model.data.LatLng
import com.gasguru.core.model.data.SearchPlace
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOf

class FakePlacesRepository(
    initialPlaces: Map<String, List<SearchPlace>> = emptyMap(),
    initialLocations: Map<String, LatLng> = emptyMap(),
) : PlacesRepository {

    private val placesByQuery = initialPlaces.toMutableMap()
    private val locationsById = initialLocations.toMutableMap()
    private var shouldThrowError = false

    val requestedQueries = mutableListOf<String>()
    val requestedLocationIds = mutableListOf<String>()

    override fun getPlaces(query: String): Flow<List<SearchPlace>> {
        requestedQueries.add(query)
        return if (shouldThrowError) {
            flow { throw Exception("Error getting places") }
        } else {
            flowOf(placesByQuery[query].orEmpty())
        }
    }

    override fun getLocationPlace(placeId: String): Flow<LatLng> {
        requestedLocationIds.add(placeId)
        return if (shouldThrowError) {
            flow { throw Exception("Error getting location") }
        } else {
            flowOf(locationsById[placeId] ?: LatLng(0.0, 0.0))
        }
    }

    fun setPlacesForQuery(query: String, places: List<SearchPlace>) {
        placesByQuery[query] = places
    }

    fun setLocationForId(placeId: String, location: LatLng) {
        locationsById[placeId] = location
    }

    fun setShouldThrowError(shouldThrow: Boolean) {
        shouldThrowError = shouldThrow
    }
}
