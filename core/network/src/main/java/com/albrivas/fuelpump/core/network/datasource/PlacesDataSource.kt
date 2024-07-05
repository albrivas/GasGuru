package com.albrivas.fuelpump.core.network.datasource

import com.google.android.gms.maps.model.LatLng
import com.google.android.libraries.places.api.model.AutocompletePrediction
import kotlinx.coroutines.flow.Flow

interface PlacesDataSource {
    fun getPlaces(query: String, countryCode: String): Flow<List<AutocompletePrediction>>
    fun getLocationPlace(placeId: String): Flow<LatLng?>
}
