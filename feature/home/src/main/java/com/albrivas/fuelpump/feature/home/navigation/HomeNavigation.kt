package com.albrivas.fuelpump.feature.home.navigation

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.compose.composable
import com.albrivas.fuelpump.feature.home.navigation.route.HomeRoute
import com.albrivas.fuelpump.feature.home.ui.HomeScreenRoute

fun NavController.navigateToHome(navOptions: NavOptions? = null) {
    this.navigate(HomeRoute, navOptions)
}

fun NavGraphBuilder.homeScreen() {
    composable<HomeRoute> {
        HomeScreenRoute()
    }
}
