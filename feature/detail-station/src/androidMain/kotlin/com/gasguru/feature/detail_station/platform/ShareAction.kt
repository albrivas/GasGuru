package com.gasguru.feature.detail_station.platform

import android.content.Intent
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext

@Composable
actual fun rememberShareAction(): (String) -> Unit {
    val context = LocalContext.current
    return { shareText ->
        val sendIntent = Intent(Intent.ACTION_SEND).apply {
            putExtra(Intent.EXTRA_TEXT, shareText)
            type = "text/plain"
        }
        context.startActivity(Intent.createChooser(sendIntent, null))
    }
}
