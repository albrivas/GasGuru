package com.gasguru.mocknetwork.di

import com.gasguru.core.common.KoinQualifiers
import com.gasguru.mocknetwork.MockRemoteDataSource
import org.koin.core.qualifier.named
import org.koin.dsl.module

fun mockModule() = module {
    single {
        MockRemoteDataSource(
            ioDispatcher = get(named(KoinQualifiers.IO_DISPATCHER)),
        )
    }
}
