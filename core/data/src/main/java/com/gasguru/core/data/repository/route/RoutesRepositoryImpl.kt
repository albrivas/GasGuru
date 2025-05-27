package com.gasguru.core.data.repository.route

import android.location.Location
import com.gasguru.core.common.IoDispatcher
import com.gasguru.core.data.mapper.toDomainRoute
import com.gasguru.core.model.data.Route
import com.gasguru.core.network.datasource.RoutesDataSource
import com.google.android.gms.maps.model.LatLng
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import javax.inject.Inject

class RoutesRepositoryImpl @Inject constructor(
    private val routesDataSource: RoutesDataSource,
    @IoDispatcher private val ioDispatcher: CoroutineDispatcher,
) : RoutesRepository {
    override fun getRoute(origin: Location, destination: Location): Flow<Route?> = flow {
        routesDataSource.getRoute(
            origin = LatLng(origin.latitude, origin.longitude),
            destination = LatLng(destination.latitude, destination.longitude)
        ).fold(ifLeft = {
            emit(null)
        }, ifRight = {
            emit(it.toDomainRoute())
        })
    }.flowOn(ioDispatcher)
}
