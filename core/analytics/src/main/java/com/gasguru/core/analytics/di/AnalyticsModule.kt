package com.gasguru.core.analytics.di

import com.gasguru.core.analytics.AnalyticsHelper
import com.gasguru.core.analytics.BuildConfig
import com.gasguru.core.analytics.LogcatAnalyticsHelper
import com.gasguru.core.analytics.MixpanelAnalyticsHelper
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module

val analyticsModule = module {
    single<AnalyticsHelper> {
        if (BuildConfig.DEBUG) LogcatAnalyticsHelper()
        else MixpanelAnalyticsHelper(context = androidContext())
    }
}
