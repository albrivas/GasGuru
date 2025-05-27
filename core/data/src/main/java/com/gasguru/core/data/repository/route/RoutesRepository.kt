package com.gasguru.core.data.repository.route

import android.location.Location
import com.gasguru.core.model.data.Route
import kotlinx.coroutines.flow.Flow

fun interface RoutesRepository {
    fun getRoute(origin: Location, destination: Location): Flow<Route?>
}
