package com.gasguru.core.ui

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.window.DialogWindowProvider
import androidx.core.view.WindowCompat
import com.gasguru.core.uikit.theme.GasGuruTheme

@Composable
fun ConfigureDialogSystemBars(invertColors: Boolean = false) {
    val view = LocalView.current
    val isDarkTheme = GasGuruTheme.colors.isDark

    LaunchedEffect(isDarkTheme, invertColors) {
        try {
            val window = (view.parent as DialogWindowProvider).window
            val insetsController = WindowCompat.getInsetsController(window, view)
            val shouldUseLightStatusBar = if (invertColors) isDarkTheme else !isDarkTheme
            insetsController.isAppearanceLightStatusBars = shouldUseLightStatusBar
            insetsController.isAppearanceLightNavigationBars = !isDarkTheme
        } catch (e: ClassCastException) {
            // Not in a dialog context
        }
    }
}
