package com.albrivas.fuelpump.navigation.navigationbar

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.NavOptions
import androidx.navigation.compose.composable
import com.albrivas.fuelpump.navigation.navigationbar.route.NavigationBarRoute
import com.albrivas.fuelpump.ui.NavigationBarHostRoute

fun NavController.navigateToNavigationBar(navOptions: NavOptions? = null) {
    navigate(NavigationBarRoute, navOptions)
}

internal fun NavGraphBuilder.navigationBarHost(
    navController: NavHostController,
) {
    composable<NavigationBarRoute> {
        NavigationBarHostRoute(navController = navController)
    }
}