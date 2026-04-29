package com.gasguru.core.uikit.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider

@Composable
fun MyApplicationTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    typography: GasGuruTypography = rememberGasGuruTypography(),
    content: @Composable () -> Unit,
) {
    val colors = if (darkTheme) DarkGasGuruColors else LightGasGuruColors

    SystemBarsEffect(darkTheme = darkTheme)

    val materialTypography = rememberTypography()

    CompositionLocalProvider(
        LocalGasGuruTypography provides typography,
        LocalGasGuruColors provides colors,
    ) {
        MaterialTheme(
            colorScheme = lightColorScheme(),
            content = content,
            typography = materialTypography,
        )
    }
}

@Composable
expect fun SystemBarsEffect(darkTheme: Boolean)
