package com.gasguru.core.uikit.theme

import android.app.Activity
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

@Composable
fun MyApplicationTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    typography: GasGuruTypography = GasGuruTheme.typography,
    content: @Composable () -> Unit,
) {
    val colors = if (darkTheme) DarkGasGuruColors else LightGasGuruColors

    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val activity = view.context as Activity
            val insetsController = WindowCompat.getInsetsController(activity.window, view)
            insetsController.isAppearanceLightStatusBars = !darkTheme
            insetsController.isAppearanceLightNavigationBars = !darkTheme
        }
    }

    CompositionLocalProvider(
        LocalGasGuruTypography provides typography,
        LocalGasGuruColors provides colors,
    ) {
        MaterialTheme(
            colorScheme = lightColorScheme(),
            content = content,
            typography = Typography
        )
    }
}
