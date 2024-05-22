package com.albrivas.fuelpump.feature.home.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import com.albrivas.fuelpump.feature.home.navigation.route.HomeTopLevelRoutes

@Composable
fun HomeNavigationBarHost(
    navController: NavHostController
) {
    NavHost(
        navController = navController,
        startDestination = HomeTopLevelRoutes.List(),
    ) {
        fuelStationListScreen()
    }
}