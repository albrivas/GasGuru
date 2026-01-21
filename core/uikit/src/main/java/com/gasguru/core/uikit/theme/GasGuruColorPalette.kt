package com.gasguru.core.uikit.theme

import androidx.compose.runtime.staticCompositionLocalOf

val LightGasGuruColors = GasGuruColors(
    primary100 = Primary100,
    primary200 = Primary200,
    primary300 = Primary300,
    primary400 = Primary400,
    primary500 = Primary500,
    primary600 = Primary600,
    primary700 = Primary700,
    primary800 = Primary800,
    primary900 = Primary900,
    textMain = TextMain,
    textSubtle = TextSubtle,
    textContrast = TextContrast,
    accentRed = AccentRed,
    accentGreen = AccentGreen,
    accentOrange = AccentOrange,
    neutralWhite = NeutralWhite,
    neutral100 = Neutral100,
    neutral200 = Neutral200,
    neutral300 = Neutral300,
    neutral400 = Neutral400,
    neutral500 = Neutral500,
    neutral600 = Neutral600,
    neutral700 = Neutral700,
    neutral800 = Neutral800,
    neutral900 = Neutral900,
    neutralBlack = NeutralBlack,
    red500 = Red500,
    secondaryLight = secondaryLight,
    isDark = false
)

val DarkGasGuruColors = GasGuruColors(
    primary100 = DarkPrimary100,
    primary200 = DarkPrimary200,
    primary300 = DarkPrimary300,
    primary400 = DarkPrimary400,
    primary500 = Primary500,
    primary600 = Primary600,
    primary700 = Primary700,
    primary800 = Primary800,
    primary900 = Primary900,
    textMain = DarkTextMain,
    textSubtle = DarkTextSubtle,
    textContrast = DarkTextContrast,
    accentRed = DarkAccentRed,
    accentGreen = DarkAccentGreen,
    accentOrange = DarkAccentOrange,
    neutralWhite = DarkNeutral100,
    neutral100 = DarkNeutral200,
    neutral200 = DarkNeutral300,
    neutral300 = DarkNeutral400,
    neutral400 = DarkNeutral500,
    neutral500 = DarkNeutral500,
    neutral600 = DarkNeutral600,
    neutral700 = DarkNeutral700,
    neutral800 = DarkNeutral800,
    neutral900 = DarkNeutral900,
    neutralBlack = DarkNeutralBlack,
    red500 = Red500,
    secondaryLight = secondaryLightDark,
    isDark = true
)

val LocalGasGuruColors = staticCompositionLocalOf { LightGasGuruColors }
