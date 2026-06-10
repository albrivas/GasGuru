package com.gasguru.core.data.repository.places

import cocoapods.GooglePlaces.GMSAutocompleteFilter
import cocoapods.GooglePlaces.GMSAutocompletePrediction
import cocoapods.GooglePlaces.GMSPlace
import cocoapods.GooglePlaces.GMSPlaceFieldCoordinate
import cocoapods.GooglePlaces.GMSPlaceFieldName
import cocoapods.GooglePlaces.GMSPlacesClient
import com.gasguru.core.model.data.LatLng
import com.gasguru.core.model.data.SearchPlace
import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.cinterop.useContents
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.withContext
import platform.Foundation.NSError
import kotlin.coroutines.resume

@OptIn(ExperimentalForeignApi::class)
class PlacesRepositoryIos(
    private val ioDispatcher: CoroutineDispatcher,
) : PlacesRepository {

    private val placesClient by lazy { GMSPlacesClient.sharedClient() }

    override fun getPlaces(query: String): Flow<List<SearchPlace>> = flow {
        if (query.isBlank()) {
            emit(emptyList())
            return@flow
        }
        emit(fetchAutocompletePredictions(query))
    }.catch { emit(emptyList()) }.flowOn(ioDispatcher)

    override fun getLocationPlace(placeId: String): Flow<LatLng> = flow {
        emit(fetchPlaceLocation(placeId))
    }.catch { emit(LatLng(latitude = 0.0, longitude = 0.0)) }.flowOn(ioDispatcher)

    private suspend fun fetchAutocompletePredictions(query: String): List<SearchPlace> =
        withContext(Dispatchers.Main) {
            suspendCancellableCoroutine { continuation ->
                val filter = GMSAutocompleteFilter().apply {
                    countries = listOf("ES")
                }
                placesClient.findAutocompletePredictionsFromQuery(
                    query = query,
                    filter = filter,
                    sessionToken = null,
                ) { results, error ->
                    if (error != null || results == null) {
                        continuation.resume(emptyList())
                        return@findAutocompletePredictionsFromQuery
                    }
                    @Suppress("UNCHECKED_CAST")
                    val predictions = results as? List<GMSAutocompletePrediction> ?: emptyList()
                    continuation.resume(
                        predictions
                            .filter { prediction -> prediction.placeID.isNotBlank() }
                            .map { prediction ->
                                SearchPlace(
                                    id = prediction.placeID,
                                    name = prediction.attributedFullText.string,
                                )
                            },
                    )
                }
            }
        }

    private suspend fun fetchPlaceLocation(placeId: String): LatLng =
        withContext(Dispatchers.Main) {
            suspendCancellableCoroutine { continuation ->
                placesClient.fetchPlaceFromPlaceID(
                    placeID = placeId,
                    placeFields = (GMSPlaceFieldCoordinate or GMSPlaceFieldName),
                    sessionToken = null,
                ) { place: GMSPlace?, error: NSError? ->
                    if (error != null || place == null) {
                        continuation.resume(LatLng(latitude = 0.0, longitude = 0.0))
                        return@fetchPlaceFromPlaceID
                    }
                    val latLng = place.coordinate.useContents {
                        LatLng(latitude = latitude, longitude = longitude)
                    }
                    continuation.resume(latLng)
                }
            }
        }
}
