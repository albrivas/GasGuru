package com.gasguru.core.analytics.di

import com.gasguru.core.analytics.AnalyticsHelper
import com.gasguru.core.analytics.LogcatAnalyticsHelper
import com.gasguru.core.analytics.MixpanelAnalyticsHelper
import com.mixpanel.android.BuildConfig
import com.mixpanel.android.mpmetrics.MixpanelAPI
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module

val analyticsModule = module {
    single<MixpanelAPI> {
        MixpanelAPI.getInstance(androidContext(), null, true)
    }
    single<AnalyticsHelper> {
        if (BuildConfig.DEBUG) {
            LogcatAnalyticsHelper()
        } else {
            MixpanelAnalyticsHelper(mixpanel = get())
        }
    }
}
