package com.gasguru.di

import com.gasguru.SplashViewModel
import com.gasguru.core.common.KoinQualifiers
import org.koin.core.module.dsl.viewModel
import org.koin.core.qualifier.named
import org.koin.dsl.module

val appModule = module {
    viewModel {
        SplashViewModel(
            fuelStation = get(),
            userData = get(),
            ioDispatcher = get(named(KoinQualifiers.IO_DISPATCHER)),
        )
    }
}
