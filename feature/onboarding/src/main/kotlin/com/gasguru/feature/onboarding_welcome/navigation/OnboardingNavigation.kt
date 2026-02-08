package com.gasguru.feature.onboarding_welcome.navigation

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.compose.composable
import com.gasguru.feature.onboarding_welcome.ui.NewOnboardingScreenRoute
import com.gasguru.feature.onboarding_welcome.ui.OnboardingFuelPreferencesRoute
import kotlinx.serialization.Serializable

fun NavController.navigateToOnboardingFuelPreferencesRoute(navOptions: NavOptions? = null) {
    this.navigate(OnboardingRoutes.OnboardingFuelPreferencesRoute, navOptions)
}

fun NavGraphBuilder.onboardingFuelPreferencesScreen() {
    composable<OnboardingRoutes.OnboardingFuelPreferencesRoute> {
        OnboardingFuelPreferencesRoute()
    }
}

fun NavController.navigateToNewOnboardingRoute(navOptions: NavOptions? = null) {
    this.navigate(OnboardingRoutes.NewOnboardingRoute, navOptions)
}

fun NavGraphBuilder.newOnboardingScreen() {
    composable<OnboardingRoutes.NewOnboardingRoute> {
        NewOnboardingScreenRoute()
    }
}

@Serializable
sealed class OnboardingRoutes {
    @Serializable
    data object OnboardingFuelPreferencesRoute : OnboardingRoutes()

    @Serializable
    data object NewOnboardingRoute : OnboardingRoutes()
}
