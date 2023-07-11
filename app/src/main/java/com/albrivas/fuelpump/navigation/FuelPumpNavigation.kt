package com.albrivas.fuelpump.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.rememberNavController
import com.albrivas.fuelpump.feature.home.ui.homeScreen
import com.albrivas.fuelpump.feature.splash.navigation.splashRoute
import com.albrivas.fuelpump.feature.splash.navigation.splashScreen

@Composable
fun MainNavigation() {
    val navController = rememberNavController()

    NavHost(navController = navController, startDestination = splashRoute) {
        splashScreen(navController)
        homeScreen(navController)
    }
}
