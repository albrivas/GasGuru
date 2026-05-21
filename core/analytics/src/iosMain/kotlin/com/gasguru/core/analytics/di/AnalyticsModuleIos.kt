package com.gasguru.core.analytics.di

import com.gasguru.core.analytics.AnalyticsHelper
import com.gasguru.core.analytics.NoOpAnalyticsHelper
import org.koin.dsl.module

val analyticsModuleIos = module {
    single<AnalyticsHelper> { NoOpAnalyticsHelper() }
}
