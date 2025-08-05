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
    primary100 = Primary100,
    primary200 = Primary200,
    primary300 = Primary300,
    primary400 = Primary400,
    primary500 = Primary500,
    primary600 = Primary600,
    primary700 = Primary700,
    primary800 = Primary800,
    primary900 = Primary900,
    textMain = Neutral100,
    textSubtle = Neutral500,
    textContrast = NeutralBlack,
    accentRed = AccentRed,
    accentGreen = AccentGreen,
    accentOrange = AccentOrange,
    neutralWhite = Neutral900,
    neutral100 = Neutral800,
    neutral200 = Neutral700,
    neutral300 = Neutral300,
    neutral400 = Neutral500,
    neutral500 = Neutral400,
    neutral600 = Neutral600,
    neutral700 = Neutral200,
    neutral800 = Neutral100,
    neutral900 = Neutral100,
    neutralBlack = NeutralWhite,
    red500 = Red500,
    secondaryLight = secondaryLightDark,
    isDark = true
)

val LocalGasGuruColors = staticCompositionLocalOf { LightGasGuruColors }
