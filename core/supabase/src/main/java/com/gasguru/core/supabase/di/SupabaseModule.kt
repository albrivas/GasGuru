package com.gasguru.core.supabase.di

import com.gasguru.core.supabase.BuildConfig
import com.gasguru.core.supabase.SupabaseManager
import com.gasguru.core.supabase.SupabaseManagerImpl
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.createSupabaseClient
import io.github.jan.supabase.postgrest.Postgrest
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class SupabaseModule {

    @Binds
    abstract fun bindSupabaseManager(
        supabaseManagerImpl: SupabaseManagerImpl,
    ): SupabaseManager
}

@Module
@InstallIn(SingletonComponent::class)
object SupabaseClientModule {

    @Provides
    @Singleton
    fun provideSupabaseClient(): SupabaseClient {
        return createSupabaseClient(
            supabaseUrl = BuildConfig.supabaseUrl,
            supabaseKey = BuildConfig.supabaseKey
        ) {
            install(Postgrest)
        }
    }
}
