package com.gasguru.navigation.navigationbar

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.compose.composable
import com.gasguru.navigation.navigationbar.route.NavigationBarRoute
import com.gasguru.ui.NavigationBarScreenRoute

fun NavController.navigateToNavigationBar(navOptions: NavOptions? = null) {
    navigate(NavigationBarRoute, navOptions)
}

internal fun NavGraphBuilder.navigationBarHost(
    navigateToDetail: (Int) -> Unit
) {
    composable<NavigationBarRoute> {
        NavigationBarScreenRoute(navigateToDetail = navigateToDetail)
    }
}
