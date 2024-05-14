package com.albrivas.fuelpump.feature.onboarding_welcome.navigation

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.compose.composable
import com.albrivas.fuelpump.feature.onboarding_welcome.ui.OnboardingFuelPreferencesRoute
import com.albrivas.fuelpump.feature.onboarding_welcome.ui.OnboardingWelcomeScreenRoute
import kotlinx.serialization.Serializable

fun NavController.navigateToOnboardingWelcomeRoute(navOptions: NavOptions? = null) {
    this.navigate(OnboardingRoutes.OnboardingWelcomeRoute, navOptions)
}

fun NavController.navigateToOnboardingFuelPreferencesRoute(navOptions: NavOptions? = null) {
    this.navigate(OnboardingRoutes.OnboardingFuelPreferencesRoute, navOptions)
}

fun NavGraphBuilder.onboardingWelcomeScreen(navigateToSelectFuel: () -> Unit) {
    composable<OnboardingRoutes.OnboardingWelcomeRoute> {
        OnboardingWelcomeScreenRoute(
            navigateToSelectFuel = navigateToSelectFuel
        )
    }
}

fun NavGraphBuilder.onboardingFuelPreferencesScreen(navigateToHome: () -> Unit) {
    composable<OnboardingRoutes.OnboardingFuelPreferencesRoute> {
        OnboardingFuelPreferencesRoute(
            navigateToHome = navigateToHome
        )
    }
}

@Serializable
sealed class OnboardingRoutes {
    @Serializable
    data object OnboardingWelcomeRoute : OnboardingRoutes()
    @Serializable
    data object OnboardingFuelPreferencesRoute : OnboardingRoutes()
}
