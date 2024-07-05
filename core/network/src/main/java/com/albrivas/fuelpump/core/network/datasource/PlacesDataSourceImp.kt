package com.albrivas.fuelpump.core.network.datasource

import android.util.Log
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.maps.model.LatLng
import com.google.android.libraries.places.api.model.AutocompletePrediction
import com.google.android.libraries.places.api.model.Place
import com.google.android.libraries.places.api.net.FetchPlaceRequest
import com.google.android.libraries.places.api.net.FindAutocompletePredictionsRequest
import com.google.android.libraries.places.api.net.PlacesClient
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class PlacesDataSourceImp @Inject constructor(
    private val placesClient: PlacesClient,
) : PlacesDataSource {
    override fun getPlaces(query: String, countryCode: String): Flow<List<AutocompletePrediction>> =
        flow {
            try {
                val request = FindAutocompletePredictionsRequest.builder()
                    .setQuery(query)
                    .setCountries(countryCode)
                    .build()
                val result = placesClient.findAutocompletePredictions(request).await()
                emit(result.autocompletePredictions)
            } catch (e: ApiException) {
                Log.e("PlacesDataSourceImp", "Error getting places", e)
                emit(emptyList())
            }
        }

    override fun getLocationPlace(placeId: String): Flow<LatLng?> = flow {
        try {
            val placeFields = listOf(Place.Field.ID, Place.Field.NAME, Place.Field.LAT_LNG)
            val request = FetchPlaceRequest.newInstance(placeId, placeFields)
            val response = placesClient.fetchPlace(request).await()
            emit(response.place.latLng)
        } catch (e: ApiException) {
            Log.e("PlacesDataSourceImp", "Error getting place location", e)
            emit(null)
        }
    }
}
