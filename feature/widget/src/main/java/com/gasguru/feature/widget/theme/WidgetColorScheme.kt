package com.gasguru.feature.widget.theme

import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.glance.material3.ColorProviders
import com.gasguru.core.uikit.theme.AccentGreen
import com.gasguru.core.uikit.theme.DarkNeutral100
import com.gasguru.core.uikit.theme.DarkNeutral300
import com.gasguru.core.uikit.theme.DarkTextContrast
import com.gasguru.core.uikit.theme.DarkTextMain
import com.gasguru.core.uikit.theme.DarkTextSubtle
import com.gasguru.core.uikit.theme.Neutral200
import com.gasguru.core.uikit.theme.NeutralWhite
import com.gasguru.core.uikit.theme.Primary400
import com.gasguru.core.uikit.theme.TextContrast
import com.gasguru.core.uikit.theme.TextMain
import com.gasguru.core.uikit.theme.TextSubtle

internal object WidgetColorScheme {
    val colors = ColorProviders(
        light = lightColorScheme(
            primary = Primary400,
            onPrimary = TextContrast,
            surface = NeutralWhite,
            onSurface = TextMain,
            onSurfaceVariant = TextSubtle,
            secondaryContainer = Neutral200,
            onSecondaryContainer = TextMain,
        ),
        dark = darkColorScheme(
            primary = AccentGreen,
            onPrimary = DarkTextContrast,
            surface = DarkNeutral100,
            onSurface = DarkTextMain,
            onSurfaceVariant = DarkTextSubtle,
            secondaryContainer = DarkNeutral300,
            onSecondaryContainer = DarkTextMain,
        ),
    )
}
