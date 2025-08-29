package com.gasguru.feature.detail_station.navigation

import androidx.compose.ui.window.DialogProperties
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.compose.composable
import androidx.navigation.compose.dialog
import com.gasguru.core.ui.ConfigureDialogSystemBars
import com.gasguru.feature.detail_station.ui.DetailStationScreenRoute

fun NavController.navigateToDetailStation(idServiceStation: Int, navOptions: NavOptions? = null) {
    navigate(DetailStationRoute(idServiceStation), navOptions)
}

fun NavController.navigateToDetailStationAsDialog(
    idServiceStation: Int,
    navOptions: NavOptions? = null,
) {
    navigate(DetailStationDialogRoute(idServiceStation), navOptions)
}

fun NavGraphBuilder.detailStationScreen(onBack: () -> Unit) {
    composable<DetailStationRoute>(
        enterTransition = {
            null
        },
        popExitTransition = {
            null
        },
    ) {
        DetailStationScreenRoute(onBack = onBack)
    }
}

fun NavGraphBuilder.detailStationScreenDialog(onBack: () -> Unit) {
    dialog<DetailStationDialogRoute>(
        dialogProperties = DialogProperties(
            usePlatformDefaultWidth = false,
            decorFitsSystemWindows = false
        )
    ) {
        ConfigureDialogSystemBars(invertColors = true)
        DetailStationScreenRoute(onBack = onBack)
    }
}
