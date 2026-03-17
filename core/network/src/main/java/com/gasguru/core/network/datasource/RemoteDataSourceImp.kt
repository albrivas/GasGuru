package com.gasguru.core.network.datasource

import arrow.core.Either
import com.gasguru.core.analytics.AnalyticsEvent
import com.gasguru.core.analytics.AnalyticsHelper
import com.gasguru.core.network.common.tryCall
import com.gasguru.core.network.model.NetworkError
import com.gasguru.core.network.model.NetworkFuelStation
import com.gasguru.core.network.retrofit.ApiService

class RemoteDataSourceImp(
    private val api: ApiService,
    private val analyticsHelper: AnalyticsHelper,
) : RemoteDataSource {

    override suspend fun getListFuelStations(): Either<NetworkError, NetworkFuelStation> {
        analyticsHelper.logEvent(event = AnalyticsEvent(type = AnalyticsEvent.Types.API_STATIONS_FETCH_STARTED))
        return tryCall { api.listFuelStations() }.also { result ->
            result.fold(
                ifLeft = {
                    analyticsHelper.logEvent(
                        event = AnalyticsEvent(type = AnalyticsEvent.Types.API_STATIONS_FETCH_FAILED)
                    )
                },
                ifRight = {
                    analyticsHelper.logEvent(
                        event = AnalyticsEvent(type = AnalyticsEvent.Types.API_STATIONS_FETCH_COMPLETED)
                    )
                },
            )
        }
    }
}
