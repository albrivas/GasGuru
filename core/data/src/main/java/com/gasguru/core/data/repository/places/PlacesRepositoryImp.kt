package com.gasguru.core.data.repository.places

import com.gasguru.core.data.mapper.toDomainModel
import com.gasguru.core.model.data.LatLng
import com.gasguru.core.model.data.SearchPlace
import com.gasguru.core.network.datasource.PlacesDataSource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
class PlacesRepositoryImp constructor(
    private val placesDataSource: PlacesDataSource
) : PlacesRepository {
    override fun getPlaces(query: String): Flow<List<SearchPlace>> =
        placesDataSource.getPlaces(query = query, countryCode = "ES").map { it.toDomainModel() }

    override fun getLocationPlace(placeId: String): Flow<LatLng> =
        placesDataSource.getLocationPlace(placeId = placeId).map {
            LatLng(
                latitude = it?.latitude ?: 0.0,
                longitude = it?.longitude ?: 0.0,
            )
        }
}
