package com.gasguru.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.captionBar
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.zIndex
import androidx.navigation.NavDestination.Companion.hasRoute
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.gasguru.core.uikit.components.divider.DividerLength
import com.gasguru.core.uikit.components.divider.DividerThickness
import com.gasguru.core.uikit.components.divider.GasGuruDivider
import com.gasguru.core.uikit.components.divider.GasGuruDividerModel
import com.gasguru.core.uikit.theme.GasGuruTheme
import com.gasguru.feature.favorite_list_station.navigation.stationListGraph
import com.gasguru.feature.profile.navigation.profileScreen
import com.gasguru.feature.station_map.navigation.route.StationMapGraph
import com.gasguru.feature.station_map.ui.StationMapScreenRoute
import com.gasguru.navigation.navigationbar.NavigationBottomBar

@Composable
fun NavigationBarScreenRoute(
    navigateToDetail: (Int) -> Unit,
    navigateToDetailAsDialog: (Int) -> Unit = navigateToDetail
) {
    NavigationBarScreen(
        navController = rememberNavController(),
        navigateToDetail = navigateToDetail,
        navigateToDetailAsDialog = navigateToDetailAsDialog
    )
}

@Composable
internal fun NavigationBarScreen(
    navController: NavHostController,
    navigateToDetail: (Int) -> Unit,
    navigateToDetailAsDialog: (Int) -> Unit = navigateToDetail,
) {
    val backStack by navController.currentBackStackEntryAsState()
    val onMap = backStack?.destination?.hasRoute<StationMapGraph.StationMapRoute>() == true

    Scaffold(
        bottomBar = {
            Column {
                GasGuruDivider(
                    model = GasGuruDividerModel(
                        color = GasGuruTheme.colors.neutral400,
                        thickness = DividerThickness.THICK,
                        length = DividerLength.FULL
                    )
                )
                NavigationBottomBar(navController = navController)
            }
        },
        contentWindowInsets = WindowInsets.captionBar
    ) { innerPadding ->
        Box(
            Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(GasGuruTheme.colors.neutral100)
        ) {
            StationMapScreenRoute(navigateToDetail = navigateToDetailAsDialog)

            if (!onMap) {
                NavHost(
                    modifier = Modifier
                        .fillMaxSize()
                        .zIndex(1f),
                    navController = navController,
                    startDestination = StationMapGraph.StationMapRoute
                ) {
                    composable<StationMapGraph.StationMapRoute> { /* no-op */ }
                    stationListGraph(navigateToDetail = navigateToDetail)
                    profileScreen()
                }
            }
        }
    }
}
