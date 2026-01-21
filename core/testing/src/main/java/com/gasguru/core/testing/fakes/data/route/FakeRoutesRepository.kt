package com.gasguru.core.testing.fakes.data.route

import com.gasguru.core.data.repository.route.RoutesRepository
import com.gasguru.core.model.data.LatLng
import com.gasguru.core.model.data.Route
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow

class FakeRoutesRepository(
    initialRoute: Route? = null,
) : RoutesRepository {

    private val routeFlow = MutableStateFlow(initialRoute)

    var lastOrigin: LatLng? = null
        private set
    var lastDestination: LatLng? = null
        private set

    override fun getRoute(origin: LatLng, destination: LatLng): Flow<Route?> {
        lastOrigin = origin
        lastDestination = destination
        return routeFlow
    }

    fun setRoute(route: Route?) {
        routeFlow.value = route
    }
}
