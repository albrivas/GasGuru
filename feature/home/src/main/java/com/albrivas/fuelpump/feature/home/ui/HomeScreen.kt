package com.albrivas.fuelpump.feature.home.ui

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.albrivas.fuelpump.core.uikit.theme.MyApplicationTheme
import com.albrivas.fuelpump.feature.home.component.NavigationBottomBar
import com.albrivas.fuelpump.feature.home.component.NavigationBarModel
import com.albrivas.fuelpump.feature.home.navigation.HomeNavigationBarHost

@Composable
fun HomeScreenRoute() {
    HomeScreen()
}

@Composable
internal fun HomeScreen(
    screenState: HomeScreenState = rememberHomeScreenState()
) {
    Scaffold(
        modifier = Modifier,
        bottomBar = {
            NavigationBottomBar(
                model = NavigationBarModel(
                    destinations = screenState.topLevelRoutes,
                    currentDestination = screenState.currentDestination,
                    onNavigateToDestination = { screenState.onNavItemClick(it) }
                ))
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            HomeNavigationBarHost(navController = screenState.navController)
        }
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
private fun HomeScreenPreview() {
    MyApplicationTheme {
        HomeScreen()
    }
}
