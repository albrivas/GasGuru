package com.gasguru.di

import com.gasguru.core.common.KoinQualifiers
import com.gasguru.core.model.data.AppEnvironment
import com.gasguru.core.supabase.datasource.RemoteDataSource
import com.gasguru.mocknetwork.MockRemoteDataSource
import kotlinx.coroutines.Dispatchers
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.koin.core.qualifier.named
import org.koin.dsl.koinApplication
import org.koin.dsl.module

@DisplayName("MockNetworkDataSource module")
class MockNetworkDataSourceTest {

    @Test
    @DisplayName(
        """
        GIVEN the mock remote data source module
        WHEN resolving AppEnvironment
        THEN it provides AppEnvironment.Mock
        """,
    )
    fun `resolves AppEnvironment as Mock`() {
        val koin = koinApplication {
            modules(
                module { single(named(KoinQualifiers.IO_DISPATCHER)) { Dispatchers.Unconfined } },
                remoteDataSourceModule(),
            )
        }.koin

        assertEquals(AppEnvironment.Mock, koin.get<AppEnvironment>())
    }

    @Test
    @DisplayName(
        """
        GIVEN the mock remote data source module
        WHEN resolving RemoteDataSource
        THEN it provides a MockRemoteDataSource instance
        """,
    )
    fun `resolves RemoteDataSource as MockRemoteDataSource`() {
        val koin = koinApplication {
            modules(
                module { single(named(KoinQualifiers.IO_DISPATCHER)) { Dispatchers.Unconfined } },
                remoteDataSourceModule(),
            )
        }.koin

        assertTrue(koin.get<RemoteDataSource>() is MockRemoteDataSource)
    }
}
