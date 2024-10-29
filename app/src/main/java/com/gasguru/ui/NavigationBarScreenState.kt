package com.gasguru.ui

import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.remember
import androidx.navigation.NavController
import androidx.navigation.NavDestination
import androidx.navigation.compose.currentBackStackEntryAsState

@Composable
fun rememberNavigationBarScreenState(navController: NavController) = remember(navController) {
    NavigationBarScreenState(navController)
}

@Stable
class NavigationBarScreenState(val navController: NavController) {

    val currentDestination: NavDestination?
        @Composable get() = navController.currentBackStackEntryAsState().value?.destination
}
