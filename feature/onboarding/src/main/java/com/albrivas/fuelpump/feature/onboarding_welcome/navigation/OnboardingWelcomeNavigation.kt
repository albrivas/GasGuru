package com.albrivas.fuelpump.feature.onboarding_welcome.navigation

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.compose.composable
import com.albrivas.fuelpump.feature.onboarding_welcome.ui.OnboardingWelcomeScreenRoute

const val onboardingWelcomeRoute = "onboarding_welcome"

fun NavController.navigateToOnboardingWelcomeRoute(navOptions: NavOptions? = null) {
    this.navigate(onboardingWelcomeRoute, navOptions)
}

fun NavGraphBuilder.onboardingWelcomeScreen(navigateToSelectFuel: () -> Unit) {
    composable(route = onboardingWelcomeRoute) {
        OnboardingWelcomeScreenRoute(navigateToSelectFuel)
    }
}