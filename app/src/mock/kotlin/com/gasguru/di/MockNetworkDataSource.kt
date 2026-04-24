package com.gasguru.di

import com.gasguru.core.supabase.datasource.RemoteDataSource
import com.gasguru.mocknetwork.MockRemoteDataSource
import com.gasguru.mocknetwork.di.mockModule
import org.koin.dsl.module

fun remoteDataSourceModule() = module {
    includes(mockModule())
    single<RemoteDataSource> { get<MockRemoteDataSource>() }
}
