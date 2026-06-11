package com.gasguru.core.analytics.di

import com.gasguru.core.analytics.AnalyticsHelper
import org.koin.dsl.module

fun provideAnalyticsModuleIos(analyticsHelper: AnalyticsHelper) = module {
    single<AnalyticsHelper> { analyticsHelper }
}
