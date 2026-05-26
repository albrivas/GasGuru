package com.gasguru.core.ui

import androidx.compose.runtime.staticCompositionLocalOf
import com.gasguru.core.analytics.AnalyticsHelper
import com.gasguru.core.analytics.NoOpAnalyticsHelper

val LocalAnalyticsHelper = staticCompositionLocalOf<AnalyticsHelper> {
    NoOpAnalyticsHelper()
}
