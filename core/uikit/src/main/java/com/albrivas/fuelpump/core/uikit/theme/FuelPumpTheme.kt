package com.albrivas.fuelpump.core.uikit.theme

import androidx.compose.runtime.Composable
import androidx.compose.runtime.ReadOnlyComposable

object FuelPumpTheme {
    val typography: FuelPumpTypography
        @Composable
        @ReadOnlyComposable
        get() = LocalFuelPumpTypography.current
}