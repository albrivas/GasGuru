package com.gasguru.core.data.repository.route

import com.gasguru.core.common.DefaultDispatcher
import com.gasguru.core.common.IoDispatcher
import com.gasguru.core.data.mapper.toDomainRoute
import com.gasguru.core.model.data.LatLng
import com.gasguru.core.model.data.Route
import com.gasguru.core.network.datasource.RoutesDataSource
import com.gasguru.core.network.model.route.NetworkLatLng
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.withContext
import javax.inject.Inject

class RoutesRepositoryImpl @Inject constructor(
    private val routesDataSource: RoutesDataSource,
    @IoDispatcher private val ioDispatcher: CoroutineDispatcher,
    @DefaultDispatcher private val defaultDispatcher: CoroutineDispatcher,
) : RoutesRepository {
    override fun getRoute(origin: LatLng, destination: LatLng): Flow<Route?> = flow {
        routesDataSource.getRoute(
            origin = NetworkLatLng(origin.latitude, origin.longitude),
            destination = NetworkLatLng(destination.latitude, destination.longitude)
        ).fold(ifLeft = {
            emit(null)
        }, ifRight = {
            val domainRoute = withContext(defaultDispatcher) {
                it.toDomainRoute()
            }
            emit(domainRoute)
        })
    }.flowOn(ioDispatcher)
}
