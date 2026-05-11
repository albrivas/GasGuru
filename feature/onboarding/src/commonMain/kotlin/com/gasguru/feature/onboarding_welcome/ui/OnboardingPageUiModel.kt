package com.gasguru.feature.onboarding_welcome.ui

import com.gasguru.feature.onboarding.generated.resources.Res
import com.gasguru.feature.onboarding.generated.resources.ic_onboarding_location
import com.gasguru.feature.onboarding.generated.resources.ic_onboarding_notifications
import com.gasguru.feature.onboarding.generated.resources.ic_onboarding_offline
import com.gasguru.feature.onboarding.generated.resources.ic_onboarding_route
import com.gasguru.feature.onboarding.generated.resources.onboarding_desc_alerts
import com.gasguru.feature.onboarding.generated.resources.onboarding_desc_offline
import com.gasguru.feature.onboarding.generated.resources.onboarding_desc_routes
import com.gasguru.feature.onboarding.generated.resources.onboarding_desc_stations
import com.gasguru.feature.onboarding.generated.resources.onboarding_title_alerts
import com.gasguru.feature.onboarding.generated.resources.onboarding_title_offline
import com.gasguru.feature.onboarding.generated.resources.onboarding_title_routes
import com.gasguru.feature.onboarding.generated.resources.onboarding_title_stations
import org.jetbrains.compose.resources.DrawableResource
import org.jetbrains.compose.resources.StringResource

enum class OnboardingPageUiModel(
    val titleRes: StringResource,
    val descriptionRes: StringResource,
    val iconRes: DrawableResource,
) {
    STATIONS(
        titleRes = Res.string.onboarding_title_stations,
        descriptionRes = Res.string.onboarding_desc_stations,
        iconRes = Res.drawable.ic_onboarding_location,
    ),
    ROUTES(
        titleRes = Res.string.onboarding_title_routes,
        descriptionRes = Res.string.onboarding_desc_routes,
        iconRes = Res.drawable.ic_onboarding_route,
    ),
    ALERTS(
        titleRes = Res.string.onboarding_title_alerts,
        descriptionRes = Res.string.onboarding_desc_alerts,
        iconRes = Res.drawable.ic_onboarding_notifications,
    ),
    OFFLINE(
        titleRes = Res.string.onboarding_title_offline,
        descriptionRes = Res.string.onboarding_desc_offline,
        iconRes = Res.drawable.ic_onboarding_offline,
    ),
}
