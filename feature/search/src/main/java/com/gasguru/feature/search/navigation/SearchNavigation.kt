package com.gasguru.feature.search.navigation

import androidx.compose.ui.window.DialogProperties
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.compose.dialog
import com.gasguru.core.ui.ConfigureDialogSystemBars
import com.gasguru.feature.search.ui.SearchScreenRoute

fun NavController.navigateToSearch(navOptions: NavOptions? = null) {
    navigate(SearchScreenRoute, navOptions)
}

fun NavGraphBuilder.searchScreen() {
    dialog<SearchScreenRoute>(
        dialogProperties = DialogProperties(
            usePlatformDefaultWidth = false,
            decorFitsSystemWindows = false
        )
    ) {
        ConfigureDialogSystemBars()

        SearchScreenRoute()
    }
}
