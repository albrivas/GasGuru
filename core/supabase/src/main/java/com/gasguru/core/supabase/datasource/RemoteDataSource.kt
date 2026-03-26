package com.gasguru.core.supabase.datasource

import arrow.core.Either
import com.gasguru.core.supabase.model.NetworkError
import com.gasguru.core.supabase.model.NetworkFuelStation

/**
 * Interface representing the remote data source for fuel stations.
 */
fun interface RemoteDataSource {
    suspend fun getListFuelStations(): Either<NetworkError, NetworkFuelStation>
}
