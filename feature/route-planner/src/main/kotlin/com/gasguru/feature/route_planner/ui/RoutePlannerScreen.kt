package com.gasguru.feature.route_planner.ui

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.gasguru.core.uikit.theme.GasGuruTheme
import com.gasguru.core.uikit.theme.MyApplicationTheme
import com.gasguru.core.uikit.theme.ThemePreviews

@Composable
fun RoutePlannerScreenRoute(onBack: () -> Unit = {}, navigateToSearch: () -> Unit = {}) {
    RoutePlannerScreen(onBack = onBack, navigateToSearch = navigateToSearch)
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun RoutePlannerScreen(onBack: () -> Unit = {}, navigateToSearch: () -> Unit = {}) {
    Scaffold(
        containerColor = GasGuruTheme.colors.neutral100,
        contentColor = GasGuruTheme.colors.neutral100,
        topBar = {
            TopAppBar(
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = GasGuruTheme.colors.neutral100,
                ),
                title = {},
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            tint = GasGuruTheme.colors.neutralBlack,
                            contentDescription = "Localized description"
                        )
                    }
                }
            )
        },
        bottomBar = {},
        modifier = Modifier
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
        ) {

        }
    }
}

@Composable
@ThemePreviews
private fun RoutePlannerScreenPreview() {
    MyApplicationTheme {
        RoutePlannerScreen()
    }
}