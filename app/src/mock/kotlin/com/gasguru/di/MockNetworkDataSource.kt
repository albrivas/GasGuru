package com.gasguru.di

import com.gasguru.core.network.datasource.RemoteDataSource
import com.gasguru.mocknetwork.MockRemoteDataSource
import com.gasguru.mocknetwork.di.mockWebServerModule
import org.koin.dsl.module

val remoteDataSourceModule = module {
    includes(mockWebServerModule)
    single<RemoteDataSource> { MockRemoteDataSource(mockWebServerManager = get()) }
}
