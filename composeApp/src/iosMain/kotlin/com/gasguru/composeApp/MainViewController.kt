package com.gasguru.composeApp

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.window.ComposeUIViewController
import androidx.lifecycle.compose.collectAsStateWithLifecycle
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

    App(
        themeMode = themeMode,
        onOpenLocationSettings = {
            NSURL(string = "app-settings:").let { url ->
                UIApplication.sharedApplication.openURL(url)
            }
        },
    )
}
