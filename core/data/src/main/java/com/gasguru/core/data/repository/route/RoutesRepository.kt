package com.gasguru.core.data.repository.route

import com.gasguru.core.model.data.LatLng
import com.gasguru.core.model.data.Route
import kotlinx.coroutines.flow.Flow

fun interface RoutesRepository {
    fun getRoute(origin: LatLng, destination: LatLng): Flow<Route?>
}
