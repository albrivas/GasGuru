package com.gasguru.core.network.datasource

import arrow.core.Either
import com.gasguru.core.network.model.NetworkError
import com.gasguru.core.network.model.route.NetworkRoutes
import com.google.android.gms.maps.model.LatLng

fun interface RoutesDataSource {
    suspend fun getRoute(origin: LatLng, destination: LatLng): Either<NetworkError, NetworkRoutes>
}
