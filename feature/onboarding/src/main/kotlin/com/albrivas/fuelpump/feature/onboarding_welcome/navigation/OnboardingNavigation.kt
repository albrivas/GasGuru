package com.albrivas.fuelpump.feature.onboarding_welcome.navigation

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.compose.composable
import com.albrivas.fuelpump.feature.onboarding_welcome.ui.OnboardingFuelPreferencesRoute
import com.albrivas.fuelpump.feature.onboarding_welcome.ui.OnboardingWelcomeScreenRoute

const val onboardingWelcomeRoute = "onboarding_welcome"
const val onboardingFuelPreferencesRoute = "onboarding_fuel_preferences"

fun NavController.navigateToOnboardingWelcomeRoute(navOptions: NavOptions? = null) {
    this.navigate(onboardingWelcomeRoute, navOptions)
}

fun NavController.navigateToOnboardingFuelPreferencesRoute(navOptions: NavOptions? = null) {
    this.navigate(onboardingFuelPreferencesRoute, navOptions)
}

fun NavGraphBuilder.onboardingWelcomeScreen(navigateToSelectFuel: () -> Unit) {
    composable(route = onboardingWelcomeRoute) {
        OnboardingWelcomeScreenRoute(
            navigateToSelectFuel = navigateToSelectFuel
        )
    }
}

fun NavGraphBuilder.onboardingFuelPreferencesScreen(navigateToHome: () -> Unit) {
    composable(route = onboardingFuelPreferencesRoute) {
        OnboardingFuelPreferencesRoute(
            navigateToHome = navigateToHome
        )
    }
}