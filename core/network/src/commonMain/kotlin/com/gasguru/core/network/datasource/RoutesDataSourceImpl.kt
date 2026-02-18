package com.gasguru.core.network.datasource

import arrow.core.Either
import com.gasguru.core.network.common.tryCall
import com.gasguru.core.network.model.NetworkError
import com.gasguru.core.network.model.route.NetworkLatLng
import com.gasguru.core.network.model.route.NetworkRoutes
import com.gasguru.core.network.request.RequestDestination
import com.gasguru.core.network.request.RequestLatLng
import com.gasguru.core.network.request.RequestLocation
import com.gasguru.core.network.request.RequestOrigin
import com.gasguru.core.network.request.RequestRoute
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.contentType

class RoutesDataSourceImpl(
    private val httpClient: HttpClient,
) : RoutesDataSource {

    override suspend fun getRoute(
        origin: NetworkLatLng,
        destination: NetworkLatLng,
    ): Either<NetworkError, NetworkRoutes> = tryCall {
        httpClient.post("directions/v2:computeRoutes") {
            contentType(ContentType.Application.Json)
            setBody(
                RequestRoute(
                    origin = RequestOrigin(
                        location = RequestLocation(
                            latLng = RequestLatLng(
                                latitude = origin.latitude,
                                longitude = origin.longitude,
                            ),
                        ),
                    ),
                    destination = RequestDestination(
                        location = RequestLocation(
                            latLng = RequestLatLng(
                                latitude = destination.latitude,
                                longitude = destination.longitude,
                            ),
                        ),
                    ),
                    travelMode = "DRIVE",
                    languageCode = "es-ES",
                    computeAlternativeRoutes = "false",
                )
            )
        }.body()
    }
}