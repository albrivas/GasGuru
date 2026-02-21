package com.gasguru.mocknetwork.di

import com.gasguru.core.common.KoinQualifiers
import com.gasguru.mocknetwork.MockWebServerManager
import com.gasguru.mocknetwork.MockWebServerManagerImp
import org.koin.android.ext.koin.androidContext
import org.koin.core.qualifier.named
import org.koin.dsl.module

val mockWebServerModule = module {
    single<MockWebServerManager> {
        MockWebServerManagerImp(
            context = androidContext(),
            ioDispatcher = get(named(KoinQualifiers.IO_DISPATCHER)),
        )
    }
}
