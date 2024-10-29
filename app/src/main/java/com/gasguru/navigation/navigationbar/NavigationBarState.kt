package com.gasguru.navigation.navigationbar

import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.remember
import androidx.navigation.NavDestination
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import com.gasguru.feature.favorite_list_station.navigation.route.StationListGraph
import com.gasguru.feature.profile.navigation.ProfileRoute
import com.gasguru.feature.station_map.navigation.route.StationMapGraph
import com.gasguru.navigation.navigationbar.route.TopLevelRoutes

@Composable
fun rememberNavigationBarState(
    navController: NavHostController
) = remember(navController) {
    NavigationBarState(navController = navController)
}

@Stable
class NavigationBarState(
    val navController: NavHostController,
) {

    val topLevelRoutes = listOf(
        TopLevelRoutes.Map(),
        TopLevelRoutes.Favorite(),
        TopLevelRoutes.Profile()
    )
    val currentDestination: NavDestination?
        @Composable get() = navController.currentBackStackEntryAsState().value?.destination

    fun onNavItemClick(route: TopLevelRoutes) =
        navController.navigatePoppingUpToStartDestination(route)

    @Suppress("IMPLICIT_CAST_TO_ANY")
    private fun NavHostController.navigatePoppingUpToStartDestination(route: TopLevelRoutes) {
        val topLevel = when (route) {
            is TopLevelRoutes.Map -> StationMapGraph.StationMapRoute
            is TopLevelRoutes.Favorite -> StationListGraph.StationListRoute
            is TopLevelRoutes.Profile -> ProfileRoute
        }
        navigate(topLevel) {
            popUpTo(graph.findStartDestination().id) {
                saveState = true
            }

            launchSingleTop = true
            restoreState = true
        }
    }
}
