package com.gasguru.di

import com.gasguru.core.model.data.AppEnvironment
import com.gasguru.core.supabase.datasource.RemoteDataSource
import com.gasguru.core.supabase.datasource.SupabaseRemoteDataSource
import io.mockk.mockk
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertSame
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.koin.dsl.koinApplication
import org.koin.dsl.module

@DisplayName("ProdDataSourceModule")
class ProdDataSourceModuleTest {

    @Test
    @DisplayName(
        """
        GIVEN the prod remote data source module
        WHEN resolving AppEnvironment
        THEN it provides AppEnvironment.Prod
        """,
    )
    fun `resolves AppEnvironment as Prod`() {
        val koin = koinApplication {
            modules(
                module { single { mockk<SupabaseRemoteDataSource>() } },
                remoteDataSourceModule(),
            )
        }.koin

        assertEquals(AppEnvironment.Prod, koin.get<AppEnvironment>())
    }

    @Test
    @DisplayName(
        """
        GIVEN the prod remote data source module
        WHEN resolving RemoteDataSource
        THEN it provides the SupabaseRemoteDataSource instance
        """,
    )
    fun `resolves RemoteDataSource as SupabaseRemoteDataSource`() {
        val supabaseRemoteDataSource = mockk<SupabaseRemoteDataSource>()
        val koin = koinApplication {
            modules(
                module { single { supabaseRemoteDataSource } },
                remoteDataSourceModule(),
            )
        }.koin

        assertSame(supabaseRemoteDataSource, koin.get<RemoteDataSource>())
    }
}
