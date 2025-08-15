package com.gasguru.feature.search.navigation

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.compose.composable
import com.gasguru.core.model.data.SearchPlace
import com.gasguru.feature.search.ui.SearchScreenRoute

fun NavController.navigateToSearch(navOptions: NavOptions? = null) {
    navigate(SearchGraph.SearchRoute, navOptions)
}

fun NavGraphBuilder.searchScreen(
    onPlaceSelected: (SearchPlace) -> Unit,
    onBackPressed: () -> Unit
) {
    composable<SearchGraph.SearchRoute> {
        SearchScreenRoute(
            onPlaceSelected = { place ->
                onPlaceSelected(place)
                onBackPressed()
            },
            onBackPressed = onBackPressed
        )
    }
}