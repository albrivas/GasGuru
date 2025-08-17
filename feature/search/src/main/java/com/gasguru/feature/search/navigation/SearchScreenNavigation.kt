package com.gasguru.feature.search.navigation

import androidx.compose.ui.window.DialogProperties
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.compose.dialog
import com.gasguru.core.model.data.SearchPlace
import com.gasguru.feature.search.ui.SearchScreenRoute

fun NavController.navigateToSearchBarScreen(
    navOptions: NavOptions? = null,
) {
    navigate(SearchScreenRoute, navOptions)
}

fun NavGraphBuilder.searchBarScreen(onBack: (SearchPlace) -> Unit) {
    dialog<SearchScreenRoute>(
        dialogProperties = DialogProperties(
            usePlatformDefaultWidth = false,
            decorFitsSystemWindows = false
        )
    ) {
        SearchScreenRoute(onPlaceSelected = onBack)
    }
}