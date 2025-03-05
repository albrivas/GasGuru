package com.gasguru.core.data.repository

import android.location.Location
import com.gasguru.core.data.mapper.toDomainModel
import com.gasguru.core.model.data.SearchPlace
import com.gasguru.core.network.datasource.PlacesDataSource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class PlacesRepositoryImp @Inject constructor(
    private val placesDataSource: PlacesDataSource
) : PlacesRepository {
    override fun getPlaces(query: String): Flow<List<SearchPlace>> =
        placesDataSource.getPlaces(query = query, countryCode = "ES").map { it.toDomainModel() }

    override fun getLocationPlace(placeId: String): Flow<Location> =
        placesDataSource.getLocationPlace(placeId = placeId).map {
            Location("").apply {
                latitude = it?.latitude ?: 0.0
                longitude = it?.longitude ?: 0.0
            }
        }
}
