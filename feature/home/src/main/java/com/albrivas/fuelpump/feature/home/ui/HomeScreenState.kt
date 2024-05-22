package com.albrivas.fuelpump.feature.home.ui

import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.remember
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.albrivas.fuelpump.feature.home.navigation.route.HomeTopLevelRoutes

@Composable
fun rememberHomeScreenState(
    navController: NavHostController = rememberNavController(),
) = remember(navController) {
    HomeScreenState(navController = navController)
}

@Stable
class HomeScreenState(
    val navController: NavHostController,
) {

    val topLevelRoutes = listOf(
        HomeTopLevelRoutes.Map(),
        HomeTopLevelRoutes.List(),
        HomeTopLevelRoutes.Profile()
    )
    val currentDestination: NavBackStackEntry?
        @Composable get() = navController.currentBackStackEntryAsState().value

    fun onNavItemClick(route: HomeTopLevelRoutes) =
        navController.navigatePoppingUpToStartDestination(route)

    private fun NavHostController.navigatePoppingUpToStartDestination(route: HomeTopLevelRoutes) {
        navigate(route) {
            popUpTo(graph.findStartDestination().id) {
                saveState = true
            }

            launchSingleTop = true
            restoreState = true
        }
    }
}