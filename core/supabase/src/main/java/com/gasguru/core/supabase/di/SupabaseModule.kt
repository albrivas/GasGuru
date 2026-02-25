package com.gasguru.core.supabase.di

import com.gasguru.core.supabase.BuildConfig
import com.gasguru.core.supabase.SupabaseManager
import com.gasguru.core.supabase.SupabaseManagerImpl
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.createSupabaseClient
import io.github.jan.supabase.postgrest.Postgrest
import org.koin.dsl.module

val supabaseModule = module {
    single<SupabaseClient> {
        createSupabaseClient(
            supabaseUrl = BuildConfig.supabaseUrl,
            supabaseKey = BuildConfig.supabaseKey,
        ) {
            install(Postgrest)
        }
    }

    single<SupabaseManager> { SupabaseManagerImpl(supabaseClient = get()) }
}
