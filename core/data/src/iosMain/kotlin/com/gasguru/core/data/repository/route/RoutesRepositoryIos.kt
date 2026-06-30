@file:OptIn(kotlinx.cinterop.ExperimentalForeignApi::class)

package com.gasguru.core.data.repository.route

import com.gasguru.core.model.data.LatLng
import com.gasguru.core.model.data.Route
import kotlinx.cinterop.CValue
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.flowOn
import platform.CoreLocation.CLLocationCoordinate2D
import platform.CoreLocation.CLLocationCoordinate2DMake
import platform.MapKit.MKDirections
import platform.MapKit.MKDirectionsRequest
import platform.MapKit.MKMapItem
import platform.MapKit.MKPlacemark
import platform.MapKit.MKRoute

class RoutesRepositoryIos(
    private val ioDispatcher: CoroutineDispatcher,
) : RoutesRepository {

    override fun getRoute(origin: LatLng, destination: LatLng): Flow<Route?> = callbackFlow {
        val request = MKDirectionsRequest()
        request.setSource(MKMapItem(placemark = MKPlacemark(coordinate = origin.toCLLocationCoordinate2D())))
        request.setDestination(MKMapItem(placemark = MKPlacemark(coordinate = destination.toCLLocationCoordinate2D())))

        val directions = MKDirections(request = request)
        directions.calculateDirectionsWithCompletionHandler { response, _ ->
            val mkRoute = response?.routes?.firstOrNull() as? MKRoute
            trySend(mkRoute?.toDomainRoute())
            close()
        }
        awaitClose { directions.cancel() }
    }.flowOn(ioDispatcher)
}

private fun LatLng.toCLLocationCoordinate2D(): CValue<CLLocationCoordinate2D> =
    CLLocationCoordinate2DMake(latitude, longitude)
