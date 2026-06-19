package com.gasguru.core.ui

import androidx.compose.runtime.Composable
import androidx.compose.ui.window.DialogProperties

actual fun fullScreenDialogProperties(): DialogProperties = DialogProperties(
    usePlatformDefaultWidth = false,
)

@Suppress("EmptyFunctionBlock")
@Composable
actual fun ConfigureDialogSystemBars(invertColors: Boolean) {
}
