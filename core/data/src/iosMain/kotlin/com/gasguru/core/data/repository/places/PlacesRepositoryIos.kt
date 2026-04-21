package com.gasguru.core.data.repository.places

import com.gasguru.core.model.data.LatLng
import com.gasguru.core.model.data.SearchPlace
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf

// V1 stub. Para iOS V2: implementar con MKLocalSearch (MapKit).
class PlacesRepositoryIos : PlacesRepository {
    override fun getPlaces(query: String): Flow<List<SearchPlace>> = flowOf(emptyList())
    override fun getLocationPlace(placeId: String): Flow<LatLng> =
        flowOf(LatLng(latitude = 0.0, longitude = 0.0))
}
