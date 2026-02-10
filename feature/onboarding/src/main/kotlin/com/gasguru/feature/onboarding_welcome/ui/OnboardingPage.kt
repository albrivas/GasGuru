package com.gasguru.feature.onboarding_welcome.ui

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import com.gasguru.feature.onboarding.R

enum class OnboardingPage(
    @StringRes val titleRes: Int,
    @StringRes val descriptionRes: Int,
    @DrawableRes val iconRes: Int,
) {
    STATIONS(
        titleRes = R.string.onboarding_title_stations,
        descriptionRes = R.string.onboarding_desc_stations,
        iconRes = R.drawable.ic_onboarding_location,
    ),
    ROUTES(
        titleRes = R.string.onboarding_title_routes,
        descriptionRes = R.string.onboarding_desc_routes,
        iconRes = R.drawable.ic_onboarding_route,
    ),
    ALERTS(
        titleRes = R.string.onboarding_title_alerts,
        descriptionRes = R.string.onboarding_desc_alerts,
        iconRes = R.drawable.ic_onboarding_notifications,
    ),
    OFFLINE(
        titleRes = R.string.onboarding_title_offline,
        descriptionRes = R.string.onboarding_desc_offline,
        iconRes = R.drawable.ic_onboarding_offline,
    ),
}
