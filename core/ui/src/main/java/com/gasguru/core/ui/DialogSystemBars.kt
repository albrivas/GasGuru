package com.gasguru.core.ui

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.window.DialogWindowProvider
import androidx.core.view.WindowCompat
import com.gasguru.core.uikit.theme.GasGuruTheme

/**
 * Configures the status bar appearance for dialogs to match the current theme.
 * This should be called within dialog composables to ensure proper status bar styling.
 *
 * @param invertColors When true, inverts the normal status bar color logic.
 *                     Useful for screens with light backgrounds in dark theme
 *                     (e.g., image detail screens where the image is light)
 */
@Composable
fun ConfigureDialogSystemBars(invertColors: Boolean = false) {
    val view = LocalView.current
    val isDarkTheme = GasGuruTheme.colors.isDark

    LaunchedEffect(isDarkTheme, invertColors) {
        try {
            val window = (view.parent as DialogWindowProvider).window
            val insetsController = WindowCompat.getInsetsController(window, view)
            // If invertColors is true, invert the normal logic
            val shouldUseLightStatusBar = if (invertColors) isDarkTheme else !isDarkTheme
            insetsController.isAppearanceLightStatusBars = shouldUseLightStatusBar
            insetsController.isAppearanceLightNavigationBars = !isDarkTheme
        } catch (e: ClassCastException) {
            // Ignore if not in a dialog context
        }
    }
}
