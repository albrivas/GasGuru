package com.gasguru.core.analytics.di

import com.gasguru.core.analytics.AnalyticsHelper
import com.gasguru.core.analytics.LogAnalyticsHelperIos
import com.gasguru.core.analytics.MixpanelAnalyticsHelperIos
import org.koin.dsl.module
import kotlin.native.Platform

val analyticsModuleIos = module {
    single<AnalyticsHelper> {
        if (Platform.isDebugBinary) {
            LogAnalyticsHelperIos()
        } else {
            MixpanelAnalyticsHelperIos()
        }
    }
}
