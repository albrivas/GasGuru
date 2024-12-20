package com.gasguru.core.data.repository.route

import com.gasguru.core.model.data.Route
import com.google.android.gms.maps.model.LatLng
import kotlinx.coroutines.flow.Flow

fun interface RoutesRepository {
    fun getRoute(origin: LatLng?, destination: LatLng?): Flow<Route>
}