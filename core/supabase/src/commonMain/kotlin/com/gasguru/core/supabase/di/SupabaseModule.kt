package com.gasguru.core.supabase.di

import com.gasguru.core.analytics.AnalyticsHelper
import com.gasguru.core.supabase.SupabaseManager
import com.gasguru.core.supabase.SupabaseManagerImpl
import com.gasguru.core.supabase.SupabaseSecrets
import com.gasguru.core.supabase.datasource.SupabaseRemoteDataSource
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.createSupabaseClient
import io.github.jan.supabase.postgrest.Postgrest
import org.koin.dsl.module

val supabaseModule = module {
    single<SupabaseClient> {
        createSupabaseClient(
            supabaseUrl = SupabaseSecrets.SUPABASE_URL,
            supabaseKey = SupabaseSecrets.SUPABASE_KEY,
        ) {
            install(Postgrest)
        }
    }

    single<SupabaseManager> { SupabaseManagerImpl(supabaseClient = get()) }

    single {
        SupabaseRemoteDataSource(
            supabaseClient = get(),
            analyticsHelper = get<AnalyticsHelper>(),
        )
    }
}
