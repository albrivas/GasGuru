package com.gasguru.core.network.datasource

import arrow.core.Either
import com.gasguru.core.network.common.tryCall
import com.gasguru.core.network.di.RouteApi
import com.gasguru.core.network.model.NetworkError
import com.gasguru.core.network.model.route.NetworkRoutes
import com.gasguru.core.network.request.RequestDestination
import com.gasguru.core.network.request.RequestLatLng
import com.gasguru.core.network.request.RequestLocation
import com.gasguru.core.network.request.RequestOrigin
import com.gasguru.core.network.request.RequestRoute
import com.gasguru.core.network.retrofit.RouteApiServices
import com.google.android.gms.maps.model.LatLng
import javax.inject.Inject

class RoutesDataSourceImpl @Inject constructor(
    @RouteApi private val routeApiServices: RouteApiServices,
) : RoutesDataSource {
    override suspend fun getRoute(
        origin: LatLng,
        destination: LatLng,
    ): Either<NetworkError, NetworkRoutes> = tryCall {
        routeApiServices.routes(
            RequestRoute(
                origin = RequestOrigin(
                    location = RequestLocation(
                        RequestLatLng(
                            origin.latitude,
                            origin.longitude
                        )
                    )
                ),
                destination = RequestDestination(
                    location = RequestLocation(
                        RequestLatLng(
                            destination.latitude,
                            destination.longitude
                        )
                    )
                ),
                travelMode = "DRIVE",
                languageCode = "es-ES"
            )
        )
    }
}
