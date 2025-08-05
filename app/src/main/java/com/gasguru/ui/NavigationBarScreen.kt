package com.gasguru.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.captionBar
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import com.gasguru.core.uikit.theme.GasGuruTheme
import com.gasguru.feature.favorite_list_station.navigation.stationListGraph
import com.gasguru.feature.profile.navigation.profileScreen
import com.gasguru.feature.station_map.navigation.route.StationMapGraph
import com.gasguru.feature.station_map.navigation.stationMapGraph
import com.gasguru.navigation.navigationbar.NavigationBottomBar

@Composable
fun NavigationBarScreenRoute(navController: NavHostController, navigateToDetail: (Int) -> Unit) {
    NavigationBarScreen(navController = navController, navigateToDetail = navigateToDetail)
}

@Composable
internal fun NavigationBarScreen(
    navController: NavHostController,
    navigateToDetail: (Int) -> Unit,
) {
    Scaffold(
        modifier = Modifier,
        bottomBar = {
            Column {
                HorizontalDivider(thickness = 1.dp, color = GasGuruTheme.colors.neutral400)
                NavigationBottomBar(navController = navController)
            }
        },
        contentWindowInsets = WindowInsets.captionBar
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(color = GasGuruTheme.colors.neutral100)
        ) {
            NavHost(
                navController = navController,
                startDestination = StationMapGraph.StationMapGraphRoute
            ) {
                stationMapGraph(navigateToDetail = navigateToDetail)
                stationListGraph(navigateToDetail = navigateToDetail)
                profileScreen()
            }
        }
    }
}
