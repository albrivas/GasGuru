package com.albrivas.fuelpump.navigation.root

import androidx.compose.runtime.Composable
import androidx.navigation.NavOptions
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.rememberNavController
import com.albrivas.fuelpump.feature.detail_station.navigation.detailStationScreen
import com.albrivas.fuelpump.feature.detail_station.navigation.navigateToDetailStation
import com.albrivas.fuelpump.feature.onboarding_welcome.navigation.OnboardingRoutes
import com.albrivas.fuelpump.feature.onboarding_welcome.navigation.navigateToOnboardingFuelPreferencesRoute
import com.albrivas.fuelpump.feature.onboarding_welcome.navigation.onboardingFuelPreferencesScreen
import com.albrivas.fuelpump.feature.onboarding_welcome.navigation.onboardingWelcomeScreen
import com.albrivas.fuelpump.navigation.navigationbar.navigateToNavigationBar
import com.albrivas.fuelpump.navigation.navigationbar.navigationBarHost

@Composable
fun MainNavigation(startDestination: Any = OnboardingRoutes.OnboardingWelcomeRoute) {
    val navController = rememberNavController()
    val navigationBarController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = startDestination,
    ) {
        val navOptions =
            NavOptions.Builder().setPopUpTo(
                route = OnboardingRoutes.OnboardingWelcomeRoute,
                inclusive = true
            ).build()
        onboardingWelcomeScreen(
            navigateToSelectFuel = navController::navigateToOnboardingFuelPreferencesRoute
        )
        onboardingFuelPreferencesScreen(
            navigateToHome = { navController.navigateToNavigationBar(navOptions) }
        )
        navigationBarHost(
            navController = navigationBarController,
            navigateToDetail = { id ->
                navController.navigateToDetailStation(id)
            }
        )
        detailStationScreen(onBack = navController::popBackStack)
    }
}
