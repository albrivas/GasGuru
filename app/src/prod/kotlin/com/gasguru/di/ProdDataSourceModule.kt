package com.gasguru.di

import com.gasguru.core.model.data.AppEnvironment
import com.gasguru.core.supabase.datasource.RemoteDataSource
import com.gasguru.core.supabase.datasource.SupabaseRemoteDataSource
import org.koin.dsl.module

fun remoteDataSourceModule() = module {
    single { AppEnvironment.Prod }
    single<RemoteDataSource> { get<SupabaseRemoteDataSource>() }
}
