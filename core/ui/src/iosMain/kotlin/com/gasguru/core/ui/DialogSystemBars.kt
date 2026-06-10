package com.gasguru.core.ui

import androidx.compose.runtime.Composable
import androidx.compose.ui.window.DialogProperties

actual fun fullScreenDialogProperties() = DialogProperties(
    usePlatformDefaultWidth = false,
)

@Composable
actual fun ConfigureDialogSystemBars(invertColors: Boolean) {
    // V1: no-op. V2: configure system bars via SwiftUI host.
}
