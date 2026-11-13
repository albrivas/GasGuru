package com.gasguru.mocknetwork.di

import com.gasguru.core.common.KoinQualifiers
import com.gasguru.core.model.data.AppEnvironment
import com.gasguru.core.supabase.datasource.RemoteDataSource
import com.gasguru.mocknetwork.MockRemoteDataSource
import org.koin.core.qualifier.named
import org.koin.dsl.module

fun mockModule() = module {
    single { AppEnvironment.Mock }
    single {
        MockRemoteDataSource(
            ioDispatcher = get(named(KoinQualifiers.IO_DISPATCHER)),
        )
    }
    // Bound here (not just in Android's app/src/mock wrapper) because iOS calls
    // mockModule() directly from Swift and needs RemoteDataSource resolvable too.
    single<RemoteDataSource> { get<MockRemoteDataSource>() }
}
