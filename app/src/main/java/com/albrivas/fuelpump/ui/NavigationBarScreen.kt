package com.albrivas.fuelpump.ui

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.captionBar
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import com.albrivas.feature.station_map.navigation.route.StationMapGraph
import com.albrivas.feature.station_map.navigation.stationMapGraph
import com.albrivas.fuelpump.feature.fuel_list_station.navigation.stationListGraph
import com.albrivas.fuelpump.feature.profile.R
import com.albrivas.fuelpump.navigation.navigationbar.NavigationBottomBar
import com.albrivas.fuelpump.navigation.navigationbar.route.TopLevelRoutes
import com.albrivas.fuelpump.profile.profileScreen

@Composable
fun NavigationBarHostRoute(navController: NavHostController, navigateToDetail: (Int) -> Unit) {
    NavigationBarHost(navController = navController, navigateToDetail = navigateToDetail)
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun NavigationBarHost(
    navController: NavHostController,
    navigateToDetail: (Int) -> Unit,
    appState: NavigationBarScreenState = rememberNavigationBarScreenState(navController),
) {
    Scaffold(
        modifier = Modifier,
        bottomBar = {
            NavigationBottomBar(navController = navController)
        },
        topBar = {
            if (TopLevelRoutes.fromRoute(appState.currentDestination?.route) == TopLevelRoutes.Profile().route) {
                CenterAlignedTopAppBar(
                    colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.White),
                    title = {
                        Text(
                            modifier = Modifier.padding(bottom = 16.dp),
                            text = stringResource(id = R.string.preferences),
                            style = MaterialTheme.typography.titleMedium
                        )
                    }
                )
            }
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
                profileScreen()
            }
        }
    }
}
