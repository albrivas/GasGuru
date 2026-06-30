package com.gasguru.core.analytics.di

import com.gasguru.core.analytics.AnalyticsHelper
import com.gasguru.core.analytics.AnalyticsSecrets
import org.koin.dsl.module

fun provideAnalyticsModuleIos(analyticsHelper: AnalyticsHelper) = module {
    single<AnalyticsHelper> { analyticsHelper }
}

fun getMixpanelToken(): String = AnalyticsSecrets.MIXPANEL_TOKEN
