package com.gasguru.core.domain.route

import com.gasguru.core.data.repository.route.RoutesRepository
import com.gasguru.core.model.data.LatLng
import com.gasguru.core.model.data.Route
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetRouteUseCase @Inject constructor(
    private val routesRepository: RoutesRepository,
) {
    operator fun invoke(origin: LatLng, destination: LatLng): Flow<Route?> =
        routesRepository.getRoute(origin, destination)
}
