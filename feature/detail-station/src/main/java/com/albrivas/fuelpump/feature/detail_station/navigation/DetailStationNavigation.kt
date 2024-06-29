package com.albrivas.fuelpump.feature.detail_station.navigation

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.compose.composable
import com.albrivas.fuelpump.feature.detail_station.ui.DetailStationScreenRoute

fun NavController.navigateToDetailStation(idServiceStation: Int, navOptions: NavOptions? = null) {
    navigate(DetailStationRoute(idServiceStation), navOptions)
}

fun NavGraphBuilder.detailStationScreen(onBack: () -> Unit) {
    composable<DetailStationRoute> {
        DetailStationScreenRoute(onBack = onBack)
    }
}