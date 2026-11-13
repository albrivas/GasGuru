package com.gasguru.mocknetwork

import arrow.core.Either
import arrow.core.left
import arrow.core.right
import com.gasguru.core.supabase.datasource.RemoteDataSource
import com.gasguru.core.supabase.model.NetworkError
import com.gasguru.core.supabase.model.SupabaseFuelStation
import com.gasguru.mocknetwork.generated.resources.Res
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext
import kotlinx.serialization.json.Json
import org.jetbrains.compose.resources.ExperimentalResourceApi

class MockRemoteDataSource(
    private val ioDispatcher: CoroutineDispatcher,
) : RemoteDataSource {

    private val json = Json { ignoreUnknownKeys = true }

    @OptIn(ExperimentalResourceApi::class)
    override suspend fun getListFuelStations(): Either<NetworkError, List<SupabaseFuelStation>> =
        try {
            val stations = withContext(ioDispatcher) {
                val jsonString = Res.readBytes(MOCK_FILE_PATH).decodeToString()
                json.decodeFromString<List<SupabaseFuelStation>>(jsonString)
            }
            stations.right()
        } catch (exception: Exception) {
            NetworkError(exception = exception).left()
        }

    private companion object {
        const val MOCK_FILE_PATH = "files/mock-fuel-stations.json"
    }
}
