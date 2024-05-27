package com.albrivas.fuelpump.ui

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import com.albrivas.fuelpump.feature.fuel_list_station.navigation.FuelStationListRoute
import com.albrivas.fuelpump.feature.fuel_list_station.navigation.fuelStationListScreen
import com.albrivas.fuelpump.navigation.navigationbar.NavigationBottomBar

@Composable
fun NavigationBarHostRoute(navController: NavHostController) {
    NavigationBarHost(navController = navController)
}

@Composable
internal fun NavigationBarHost(
    navController: NavHostController,
) {
    Scaffold(
        modifier = Modifier,
        bottomBar = {
            NavigationBottomBar(navController = navController)
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            NavHost(navController = navController, startDestination = FuelStationListRoute) {
                fuelStationListScreen()
            }
        }
    }
}