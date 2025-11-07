package com.gasguru.navigation.root

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.navigation.NavOptions
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.rememberNavController
import com.gasguru.feature.detail_station.navigation.detailStationScreen
import com.gasguru.feature.detail_station.navigation.detailStationScreenDialog
import com.gasguru.feature.detail_station.navigation.navigateToDetailStation
import com.gasguru.feature.detail_station.navigation.navigateToDetailStationAsDialog
import com.gasguru.feature.onboarding_welcome.navigation.OnboardingRoutes
import com.gasguru.feature.onboarding_welcome.navigation.navigateToOnboardingFuelPreferencesRoute
import com.gasguru.feature.onboarding_welcome.navigation.onboardingFuelPreferencesScreen
import com.gasguru.feature.onboarding_welcome.navigation.onboardingWelcomeScreen
import com.gasguru.navigation.DeepLinkEvent
import com.gasguru.navigation.DeepLinkManager
import com.gasguru.navigation.navigationbar.navigateToNavigationBar
import com.gasguru.navigation.navigationbar.navigationBarHost

@Composable
fun GasGuruNavHost(
    deepLinkManager: DeepLinkManager,
    startDestination: Any = OnboardingRoutes.OnboardingWelcomeRoute
) {
    val navController = rememberNavController()
    
    LaunchedEffect(Unit) {
        deepLinkManager.deepLinkEvents.collect { event ->
            when (event) {
                is DeepLinkEvent.NavigateToDetailStation -> {
                    navController.navigateToDetailStationAsDialog(event.stationId)
                }
            }
        }
    }

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
            navigateToDetail = { id ->
                navController.navigateToDetailStation(id)
            },
            navigateToDetailAsDialog = { id ->
                navController.navigateToDetailStationAsDialog(id)
            }
        )
        detailStationScreen(onBack = navController::popBackStack)
        detailStationScreenDialog(onBack = navController::popBackStack)
    }
}
