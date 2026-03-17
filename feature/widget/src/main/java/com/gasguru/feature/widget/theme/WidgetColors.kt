package com.gasguru.feature.widget.theme

import androidx.glance.unit.ColorProvider
import com.gasguru.core.uikit.theme.AccentGreen
import com.gasguru.core.uikit.theme.AccentOrange
import com.gasguru.core.uikit.theme.AccentRed

/**
 * Custom color providers for widget-specific accents (price category chips).
 * Solid colors for chip text; alpha variants (16%) for chip background — mirrors StatusChip.
 * General text/background colors come from GlanceTheme.colors.* inside composables.
 */
internal object WidgetColors {
    // Solid — chip text color
    val accentGreen: ColorProvider = ColorProvider(AccentGreen)
    val accentOrange: ColorProvider = ColorProvider(AccentOrange)
    val accentRed: ColorProvider = ColorProvider(AccentRed)

    // 16% alpha — chip background color (matches StatusChip's color.copy(alpha = 0.16f))
    val accentGreenAlpha: ColorProvider = ColorProvider(AccentGreen.copy(alpha = 0.16f))
    val accentOrangeAlpha: ColorProvider = ColorProvider(AccentOrange.copy(alpha = 0.16f))
    val accentRedAlpha: ColorProvider = ColorProvider(AccentRed.copy(alpha = 0.16f))
}
