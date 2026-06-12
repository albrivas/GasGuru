package com.gasguru.di

import com.gasguru.composeApp.bridge.IosBridge
import com.gasguru.composeApp.bridge.IosBridgeImpl
import com.gasguru.core.common.KoinQualifiers
import com.gasguru.splash.SplashViewModel
import org.koin.core.module.dsl.viewModel
import org.koin.core.qualifier.named
import org.koin.dsl.module

@Suppress("DeprecatedKoinApi")
fun appShellModule() = module {
    viewModel {
        SplashViewModel(
            fuelStation = get(),
            userData = get(),
            ioDispatcher = get(named(KoinQualifiers.IO_DISPATCHER)),
        )
    }
    single<IosBridge> { IosBridgeImpl(deepLinkStateHolder = get()) }
}
