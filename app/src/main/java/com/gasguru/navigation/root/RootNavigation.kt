package com.gasguru.navigation.root

import androidx.compose.runtime.Composable
import androidx.navigation.NavOptions
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.rememberNavController
import com.gasguru.feature.detail_station.navigation.detailStationScreen
import com.gasguru.feature.detail_station.navigation.navigateToDetailStation
import com.gasguru.feature.onboarding_welcome.navigation.OnboardingRoutes
import com.gasguru.feature.onboarding_welcome.navigation.navigateToOnboardingFuelPreferencesRoute
import com.gasguru.feature.onboarding_welcome.navigation.onboardingFuelPreferencesScreen
import com.gasguru.feature.onboarding_welcome.navigation.onboardingWelcomeScreen
import com.gasguru.navigation.navigationbar.navigateToNavigationBar
import com.gasguru.navigation.navigationbar.navigationBarHost

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
