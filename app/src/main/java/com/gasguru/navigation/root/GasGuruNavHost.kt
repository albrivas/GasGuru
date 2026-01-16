package com.gasguru.navigation.root

import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.rememberNavController
import com.gasguru.feature.detail_station.navigation.detailStationScreen
import com.gasguru.feature.detail_station.navigation.detailStationScreenDialog
import com.gasguru.feature.onboarding_welcome.navigation.OnboardingRoutes
import com.gasguru.feature.onboarding_welcome.navigation.onboardingFuelPreferencesScreen
import com.gasguru.feature.onboarding_welcome.navigation.onboardingWelcomeScreen
import com.gasguru.navigation.LocalNavigationManager
import com.gasguru.navigation.graphs.routeSearchGraph
import com.gasguru.navigation.handler.NavigationHandler
import com.gasguru.navigation.manager.NavigationManager
import com.gasguru.navigation.navigationbar.navigationBarHost

@Composable
fun GasGuruNavHost(
    navigationManager: NavigationManager,
    startDestination: Any = OnboardingRoutes.OnboardingWelcomeRoute,
) {
    val navController = rememberNavController()
    val navigationHandler = remember(navController) { NavigationHandler(navController = navController) }

    CompositionLocalProvider(LocalNavigationManager provides navigationManager) {
        LaunchedEffect(Unit) {
            navigationManager.navigationFlow.collect { command ->
                navigationHandler.handle(command = command)
            }
        }

        NavHost(
            navController = navController,
            startDestination = startDestination,
        ) {
            onboardingWelcomeScreen()
            onboardingFuelPreferencesScreen()
            navigationBarHost()
            detailStationScreen()
            detailStationScreenDialog()
            routeSearchGraph()
        }
    }
}
