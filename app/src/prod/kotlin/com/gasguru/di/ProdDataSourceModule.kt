package com.gasguru.di

import com.gasguru.core.supabase.datasource.RemoteDataSource
import com.gasguru.core.supabase.datasource.SupabaseRemoteDataSource
import org.koin.dsl.module

val remoteDataSourceModule = module {
    single<RemoteDataSource> { get<SupabaseRemoteDataSource>() }
}
