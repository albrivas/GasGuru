package com.gasguru.feature.search.ui

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.gasguru.core.components.searchbar.GasGuruSearchBar
import com.gasguru.core.components.searchbar.GasGuruSearchBarModel
import com.gasguru.core.model.data.SearchPlace
import com.gasguru.core.uikit.theme.MyApplicationTheme
import com.gasguru.core.uikit.theme.ThemePreviews

@Composable
fun SearchScreenRoute(
    onPlaceSelected: (SearchPlace) -> Unit,
    onBackPressed: () -> Unit = {}
) {
    SearchScreen(
        onPlaceSelected = onPlaceSelected,
        onBackPressed = onBackPressed
    )
}

@Composable
internal fun SearchScreen(
    onPlaceSelected: (SearchPlace) -> Unit,
    onBackPressed: () -> Unit = {}
) {
    Box(modifier = Modifier.fillMaxSize()) {
        GasGuruSearchBar(
            model = GasGuruSearchBarModel(
                onPlaceSelected = onPlaceSelected,
                onRecentSearchClicked = onPlaceSelected,
                onBackPressed = onBackPressed,
                alwaysActive = true,
            )
        )
    }
}

@Composable
@ThemePreviews
fun SearchScreenPreview() {
    MyApplicationTheme {
        SearchScreenRoute(
            onPlaceSelected = {},
            onBackPressed = {}
        )
    }
}