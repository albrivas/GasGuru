package com.gasguru.core.testing.fakes.data.network

import arrow.core.Either
import com.gasguru.core.supabase.datasource.RemoteDataSource
import com.gasguru.core.supabase.model.NetworkError
import com.gasguru.core.supabase.model.SupabaseFuelStation

class FakeRemoteDataSource(
    var result: Either<NetworkError, List<SupabaseFuelStation>> = Either.Right(emptyList()),
) : RemoteDataSource {
    var getListFuelStationsCalls = 0
        private set

    override suspend fun getListFuelStations(): Either<NetworkError, List<SupabaseFuelStation>> {
        getListFuelStationsCalls += 1
        return result
    }
}
