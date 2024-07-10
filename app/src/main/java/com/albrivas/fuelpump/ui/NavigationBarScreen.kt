package com.albrivas.fuelpump.ui

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.captionBar
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import com.albrivas.feature.station_map.navigation.route.StationMapGraph
import com.albrivas.feature.station_map.navigation.stationMapGraph
import com.albrivas.fuelpump.feature.fuel_list_station.navigation.stationListGraph
import com.albrivas.fuelpump.navigation.navigationbar.NavigationBottomBar

@Composable
fun NavigationBarHostRoute(navController: NavHostController, navigateToDetail: (Int) -> Unit) {
    NavigationBarHost(navController = navController, navigateToDetail = navigateToDetail)
}

@Composable
internal fun NavigationBarHost(
    navController: NavHostController,
    navigateToDetail: (Int) -> Unit
) {
    Scaffold(
        modifier = Modifier,
        bottomBar = {
            NavigationBottomBar(navController = navController)
        },
        contentWindowInsets = WindowInsets.captionBar
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            NavHost(
                navController = navController,
                startDestination = StationMapGraph.StationMapGraphRoute
            ) {
                stationMapGraph(navigateToDetail = navigateToDetail)
                stationListGraph(navigateToDetail = navigateToDetail)
            }
        }
    }
}
