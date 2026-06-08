package com.gasguru.feature.detail_station.platform

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import platform.UIKit.UIActivityViewController

@Composable
actual fun rememberShareAction(): (String) -> Unit = remember {
    {
            shareText ->
        val sheet = UIActivityViewController(
            activityItems = listOf(shareText),
            applicationActivities = null,
        )
        sheet.completionWithItemsHandler = { _, _, _, _ -> dismissOverlayWindow() }
        presentInOverlayWindow(sheet)
    }
}
