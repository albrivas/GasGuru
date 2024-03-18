package com.albrivas.fuelpump.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.rememberNavController
import com.albrivas.fuelpump.feature.home.navigation.homeScreen
import com.albrivas.fuelpump.feature.home.navigation.navigateToHome
import com.albrivas.fuelpump.feature.onboarding_welcome.navigation.navigateToOnboardingFuelPreferencesRoute
import com.albrivas.fuelpump.feature.onboarding_welcome.navigation.navigateToOnboardingWelcomeRoute
import com.albrivas.fuelpump.feature.onboarding_welcome.navigation.onboardingFuelPreferencesScreen
import com.albrivas.fuelpump.feature.onboarding_welcome.navigation.onboardingWelcomeScreen
import com.albrivas.fuelpump.feature.splash.navigation.splashRoute
import com.albrivas.fuelpump.feature.splash.navigation.splashScreen

@Composable
fun MainNavigation() {
    val navController = rememberNavController()

    NavHost(navController = navController, startDestination = splashRoute) {
        splashScreen(
            navigateToOnboarding = navController::navigateToOnboardingWelcomeRoute,
            navigateToHome = navController::navigateToHome
        )
        onboardingWelcomeScreen(
            navigateToSelectFuel = navController::navigateToOnboardingFuelPreferencesRoute
        )
        onboardingFuelPreferencesScreen(
            navigateToHome = navController::navigateToHome
        )
        homeScreen()
    }
}
