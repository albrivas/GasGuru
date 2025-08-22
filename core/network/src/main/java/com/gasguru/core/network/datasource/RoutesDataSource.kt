package com.gasguru.core.network.datasource

import arrow.core.Either
import com.gasguru.core.network.model.NetworkError
import com.gasguru.core.network.model.route.NetworkLatLng
import com.gasguru.core.network.model.route.NetworkRoutes

fun interface RoutesDataSource {
    suspend fun getRoute(
        origin: NetworkLatLng,
        destination: NetworkLatLng,
    ): Either<NetworkError, NetworkRoutes>
}
