package com.gasguru.composeApp

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.window.ComposeUIViewController
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.gasguru.feature.onboarding_welcome.navigation.OnboardingRoutes
import com.gasguru.navigation.navigationbar.route.NavigationBarRoute
import com.gasguru.splash.SplashUiState
import com.gasguru.splash.SplashViewModel
import org.koin.compose.viewmodel.koinViewModel
import platform.Foundation.NSURL
import platform.UIKit.UIApplication
import platform.UIKit.UIViewController

@Suppress("FunctionName")
fun MainViewController(): UIViewController = ComposeUIViewController { GasGuruIosApp() }

@Composable
private fun GasGuruIosApp() {
    val viewModel = koinViewModel<SplashViewModel>()
    val themeMode by viewModel.themeMode.collectAsStateWithLifecycle()
    val uiStateResult by viewModel.uiState.collectAsStateWithLifecycle()

    val uiState = uiStateResult.getOrNull() ?: SplashUiState.Loading

    if (uiState == SplashUiState.Loading) return

    val startDestination = when {
        uiState is SplashUiState.Success && uiState.isOnboardingSuccess -> NavigationBarRoute
        else -> OnboardingRoutes.NewOnboardingRoute
    }

    App(
        themeMode = themeMode,
        startDestination = startDestination,
        onOpenLocationSettings = {
            NSURL(string = "app-settings:").let { url ->
                UIApplication.sharedApplication.openURL(url)
            }
        },
    )
}
