package com.gasguru.core.network.datasource

import arrow.core.Either
import com.gasguru.core.analytics.AnalyticsHelper
import com.gasguru.core.network.analytics.trackApiStationsFetchFailed
import com.gasguru.core.network.common.tryCall
import com.gasguru.core.network.model.NetworkError
import com.gasguru.core.network.model.NetworkFuelStation
import com.gasguru.core.network.retrofit.ApiService

class RemoteDataSourceImp(
    private val api: ApiService,
    private val analyticsHelper: AnalyticsHelper,
) : RemoteDataSource {

    override suspend fun getListFuelStations(): Either<NetworkError, NetworkFuelStation> {
        return tryCall { api.listFuelStations() }.also { result ->
            result.fold(
                ifLeft = { networkError ->
                    analyticsHelper.trackApiStationsFetchFailed(
                        errorMessage = networkError.exception.message.orEmpty(),
                        errorType = networkError.exception::class.simpleName.orEmpty(),
                    )
                },
                ifRight = {},
            )
        }
    }
}
