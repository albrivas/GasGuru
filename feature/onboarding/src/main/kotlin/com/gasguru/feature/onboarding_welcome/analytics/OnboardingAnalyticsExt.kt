package com.gasguru.feature.onboarding_welcome.analytics

import com.gasguru.core.analytics.AnalyticsEvent
import com.gasguru.core.analytics.AnalyticsHelper

fun AnalyticsHelper.trackOnboardingStarted() {
    logEvent(AnalyticsEvent(type = AnalyticsEvent.Types.ONBOARDING_STARTED))
}

fun AnalyticsHelper.trackOnboardingPageViewed(page: Int) {
    logEvent(
        AnalyticsEvent(
            type = AnalyticsEvent.Types.ONBOARDING_PAGE_VIEWED,
            extras = listOf(
                AnalyticsEvent.Param(key = AnalyticsEvent.ParamKeys.PAGE_NUMBER, value = page.toString()),
            ),
        )
    )
}

fun AnalyticsHelper.trackOnboardingSkipped(page: Int) {
    logEvent(
        AnalyticsEvent(
            type = AnalyticsEvent.Types.ONBOARDING_SKIPPED,
            extras = listOf(
                AnalyticsEvent.Param(key = AnalyticsEvent.ParamKeys.PAGE_NUMBER, value = page.toString()),
            ),
        )
    )
}

fun AnalyticsHelper.trackOnboardingCompleted() {
    logEvent(AnalyticsEvent(type = AnalyticsEvent.Types.ONBOARDING_COMPLETED))
}
