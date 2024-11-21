package com.gasguru.feature.detail_station.navigation

import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.core.tween
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.compose.composable
import com.gasguru.feature.detail_station.ui.DetailStationScreenRoute

fun NavController.navigateToDetailStation(idServiceStation: Int, navOptions: NavOptions? = null) {
    navigate(DetailStationRoute(idServiceStation), navOptions)
}

fun NavGraphBuilder.detailStationScreen(onBack: () -> Unit) {
    composable<DetailStationRoute>(
        enterTransition = {
            return@composable slideIntoContainer(
                AnimatedContentTransitionScope.SlideDirection.Start, tween(700)
            )
        },
        popExitTransition = {
            return@composable slideOutOfContainer(
                AnimatedContentTransitionScope.SlideDirection.End, tween(700)
            )
        },
    ) {
        DetailStationScreenRoute(onBack = onBack)
    }
}
