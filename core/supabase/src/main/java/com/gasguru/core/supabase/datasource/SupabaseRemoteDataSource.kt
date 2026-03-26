package com.gasguru.core.supabase.datasource

import arrow.core.left
import arrow.core.right
import com.gasguru.core.analytics.AnalyticsEvent
import com.gasguru.core.analytics.AnalyticsHelper
import com.gasguru.core.supabase.model.NetworkError
import com.gasguru.core.supabase.model.SupabaseFuelStation
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.postgrest.from

class SupabaseRemoteDataSource(
    private val supabaseClient: SupabaseClient,
    private val analyticsHelper: AnalyticsHelper,
) : RemoteDataSource {

    companion object {
        private const val TABLE_FUEL_STATION = "fuel-station"
    }

    override suspend fun getListFuelStations() =
        try {
            analyticsHelper.logEvent(
                event = AnalyticsEvent(type = AnalyticsEvent.Types.API_STATIONS_FETCH_STARTED),
            )
            val stations = supabaseClient
                .from(TABLE_FUEL_STATION)
                .select()
                .decodeList<SupabaseFuelStation>()

            analyticsHelper.logEvent(
                event = AnalyticsEvent(type = AnalyticsEvent.Types.API_STATIONS_FETCH_COMPLETED),
            )
            stations.right()
        } catch (exception: Exception) {
            analyticsHelper.logEvent(
                event = AnalyticsEvent(
                    type = AnalyticsEvent.Types.API_STATIONS_FETCH_FAILED,
                    extras = listOf(
                        AnalyticsEvent.Param(
                            key = AnalyticsEvent.ParamKeys.ERROR_MESSAGE,
                            value = exception.message.orEmpty(),
                        ),
                        AnalyticsEvent.Param(
                            key = AnalyticsEvent.ParamKeys.ERROR_TYPE,
                            value = exception::class.simpleName.orEmpty(),
                        ),
                    ),
                ),
            )
            NetworkError(exception = exception).left()
        }
}
