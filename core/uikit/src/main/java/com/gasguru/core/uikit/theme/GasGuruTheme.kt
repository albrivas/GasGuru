package com.gasguru.core.uikit.theme

import androidx.compose.runtime.Composable
import androidx.compose.runtime.ReadOnlyComposable

object GasGuruTheme {
    val typography: GasGuruTypography
        @Composable
        @ReadOnlyComposable
        get() = LocalGasGuruTypography.current
}
