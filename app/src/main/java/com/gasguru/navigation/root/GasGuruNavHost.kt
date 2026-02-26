package com.gasguru.navigation.root

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.rememberNavController
import com.gasguru.feature.detail_station.navigation.detailStationScreen
import com.gasguru.feature.detail_station.navigation.detailStationScreenDialog
import com.gasguru.feature.onboarding_welcome.navigation.capacityTankScreen
import com.gasguru.feature.onboarding_welcome.navigation.OnboardingRoutes
import com.gasguru.feature.onboarding_welcome.navigation.newOnboardingScreen
import com.gasguru.feature.onboarding_welcome.navigation.onboardingFuelPreferencesScreen
import com.gasguru.navigation.LocalNavigationManager
import com.gasguru.navigation.graphs.routeSearchGraph
import com.gasguru.navigation.handler.NavigationHandler
import com.gasguru.navigation.navigationbar.navigationBarHost

@Composable
fun GasGuruNavHost(
    startDestination: Any = OnboardingRoutes.NewOnboardingRoute,
) {
    val navController = rememberNavController()
    val navigationManager = LocalNavigationManager.current
    val navigationHandler = remember(navController) { NavigationHandler(navController = navController) }

    LaunchedEffect(Unit) {
        navigationManager.navigationFlow.collect { command ->
            navigationHandler.handle(command = command)
        }
    }

    NavHost(
        navController = navController,
        startDestination = startDestination,
    ) {
        newOnboardingScreen()
        onboardingFuelPreferencesScreen()
        capacityTankScreen()
        navigationBarHost()
        detailStationScreen()
        detailStationScreenDialog()
        routeSearchGraph()
    }
}
