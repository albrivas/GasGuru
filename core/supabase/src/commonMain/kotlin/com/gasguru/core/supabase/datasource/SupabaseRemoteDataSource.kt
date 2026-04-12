package com.gasguru.core.supabase.datasource

import arrow.core.left
import arrow.core.right
import com.gasguru.core.analytics.AnalyticsHelper
import com.gasguru.core.supabase.analytics.trackApiStationsFetchFailed
import com.gasguru.core.supabase.model.NetworkError
import com.gasguru.core.supabase.model.SupabaseFuelStation
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.postgrest.from

class SupabaseRemoteDataSource(
    private val supabaseClient: SupabaseClient,
    private val analyticsHelper: AnalyticsHelper,
) : RemoteDataSource {

    companion object {
        private const val TABLE_FUEL_STATION = "fuel_stations"
    }

    override suspend fun getListFuelStations() =
        try {
            val stations = supabaseClient
                .from(TABLE_FUEL_STATION)
                .select()
                .decodeList<SupabaseFuelStation>()
            stations.right()
        } catch (exception: Exception) {
            analyticsHelper.trackApiStationsFetchFailed(
                errorMessage = exception.message.orEmpty(),
                errorType = exception::class.simpleName.orEmpty(),
            )
            NetworkError(exception = exception).left()
        }
}
