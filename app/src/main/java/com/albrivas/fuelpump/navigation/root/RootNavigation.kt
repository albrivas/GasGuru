package com.albrivas.fuelpump.navigation.root

import androidx.compose.runtime.Composable
import androidx.navigation.NavOptions
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.rememberNavController
import com.albrivas.fuelpump.feature.onboarding_welcome.navigation.navigateToOnboardingFuelPreferencesRoute
import com.albrivas.fuelpump.feature.onboarding_welcome.navigation.navigateToOnboardingWelcomeRoute
import com.albrivas.fuelpump.feature.onboarding_welcome.navigation.onboardingFuelPreferencesScreen
import com.albrivas.fuelpump.feature.onboarding_welcome.navigation.onboardingWelcomeScreen
import com.albrivas.fuelpump.feature.splash.navigation.SplashRoute
import com.albrivas.fuelpump.feature.splash.navigation.splashScreen
import com.albrivas.fuelpump.navigation.navigationbar.navigateToNavigationBar
import com.albrivas.fuelpump.navigation.navigationbar.navigationBarHost

@Composable
fun MainNavigation() {
    val navController = rememberNavController()
    val navigationBarController = rememberNavController()

    NavHost(navController = navController, startDestination = SplashRoute) {
        val navOptions =
            NavOptions.Builder().setPopUpTo(SplashRoute, true).build()
        splashScreen(
            navigateToOnboarding = navController::navigateToOnboardingWelcomeRoute,
            navigateToHome = { navController.navigateToNavigationBar(navOptions) }
        )
        onboardingWelcomeScreen(
            navigateToSelectFuel = navController::navigateToOnboardingFuelPreferencesRoute
        )
        onboardingFuelPreferencesScreen(
            navigateToHome = { navController.navigateToNavigationBar(navOptions) }
        )
        navigationBarHost(navController = navigationBarController)
    }
}
