package com.gasguru.core.common

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import org.koin.core.qualifier.named
import org.koin.dsl.module

val coroutineModule = module {
    single<CoroutineDispatcher>(named(KoinQualifiers.DEFAULT_DISPATCHER)) { Dispatchers.Default }
    single<CoroutineDispatcher>(named(KoinQualifiers.IO_DISPATCHER)) { Dispatchers.IO }
    single<CoroutineDispatcher>(named(KoinQualifiers.MAIN_DISPATCHER)) { Dispatchers.Main }
    single<CoroutineDispatcher>(named(KoinQualifiers.MAIN_IMMEDIATE_DISPATCHER)) { Dispatchers.Main.immediate }

    single<CoroutineScope>(named(KoinQualifiers.APPLICATION_SCOPE)) {
        CoroutineScope(SupervisorJob() + get<CoroutineDispatcher>(named(KoinQualifiers.DEFAULT_DISPATCHER)))
    }
    single<CoroutineScope>(named(KoinQualifiers.MAIN_SCOPE)) {
        CoroutineScope(SupervisorJob() + get<CoroutineDispatcher>(named(KoinQualifiers.MAIN_DISPATCHER)))
    }
    single<CoroutineScope>(named(KoinQualifiers.IO_SCOPE)) {
        CoroutineScope(SupervisorJob() + get<CoroutineDispatcher>(named(KoinQualifiers.IO_DISPATCHER)))
    }
}
