package com.gasguru.core.ui

import androidx.compose.runtime.Composable
import androidx.compose.ui.window.DialogProperties

@Composable
expect fun ConfigureDialogSystemBars(invertColors: Boolean = false)

expect fun fullScreenDialogProperties(): DialogProperties
