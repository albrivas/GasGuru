package com.gasguru.mocknetwork

import android.content.Context
import arrow.core.Either
import arrow.core.left
import arrow.core.right
import com.gasguru.core.supabase.datasource.RemoteDataSource
import com.gasguru.core.supabase.model.NetworkError
import com.gasguru.core.supabase.model.SupabaseFuelStation
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext
import kotlinx.serialization.json.Json

class MockRemoteDataSource(
    private val context: Context,
    private val ioDispatcher: CoroutineDispatcher,
) : RemoteDataSource {

    private val json = Json { ignoreUnknownKeys = true }

    override suspend fun getListFuelStations(): Either<NetworkError, List<SupabaseFuelStation>> =
        try {
            val stations = withContext(ioDispatcher) {
                val jsonString = context.assets
                    .open(MOCK_FILE)
                    .bufferedReader()
                    .use { reader -> reader.readText() }
                json.decodeFromString<List<SupabaseFuelStation>>(jsonString)
            }
            stations.right()
        } catch (exception: Exception) {
            NetworkError(exception = exception).left()
        }

    private companion object {
        const val MOCK_FILE = "mock-fuel-stations.json"
    }
}
