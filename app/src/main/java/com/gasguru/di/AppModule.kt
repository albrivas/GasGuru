package com.gasguru.di

import com.gasguru.BuildConfig
import com.gasguru.SplashViewModel
import com.gasguru.core.common.KoinQualifiers
import com.mixpanel.android.mpmetrics.MixpanelAPI
import org.koin.android.ext.koin.androidContext
import org.koin.core.module.dsl.viewModel
import org.koin.core.qualifier.named
import org.koin.dsl.module

val appModule = module {
    single<MixpanelAPI> {
        MixpanelAPI.getInstance(androidContext(), BuildConfig.mixpanelProjectToken, true)
    }
    viewModel {
        SplashViewModel(
            fuelStation = get(),
            userData = get(),
            ioDispatcher = get(named(KoinQualifiers.IO_DISPATCHER)),
        )
    }
}
