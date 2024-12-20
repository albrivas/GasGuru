package com.gasguru.core.network.retrofit

import com.gasguru.core.network.model.route.NetworkRoutes
import com.gasguru.core.network.request.RequestRoute
import retrofit2.http.Body
import retrofit2.http.POST

fun interface RouteApiServices {

    companion object {
        const val ROUTES_PATH = "directions/v2:computeRoutes"
    }


    @POST(ROUTES_PATH)
    suspend fun routes(@Body requestRoute: RequestRoute): NetworkRoutes
}