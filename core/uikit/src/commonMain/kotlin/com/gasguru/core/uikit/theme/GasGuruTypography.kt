package com.gasguru.core.uikit.theme

import androidx.compose.runtime.Composable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp

data class GasGuruTypography(
    val h1: TextStyle = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Bold,
        fontSize = 36.sp,
        lineHeight = 44.sp,
    ),
    val h2: TextStyle = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Bold,
        fontSize = 32.sp,
        lineHeight = 40.sp,
    ),
    val h3: TextStyle = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Bold,
        fontSize = 28.sp,
        lineHeight = 36.sp,
    ),
    val h4: TextStyle = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Bold,
        fontSize = 24.sp,
        lineHeight = 32.sp,
    ),
    val h5: TextStyle = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Bold,
        fontSize = 20.sp,
        lineHeight = 28.sp,
    ),
    val h6: TextStyle = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Bold,
        fontSize = 18.sp,
        lineHeight = 24.sp,
    ),
    val displayRegular: TextStyle = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Normal,
        fontSize = 48.sp,
        lineHeight = 58.sp,
    ),
    val displayBold: TextStyle = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Bold,
        fontSize = 48.sp,
        lineHeight = 58.sp,
    ),
    val baseRegular: TextStyle = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Normal,
        fontSize = 16.sp,
        lineHeight = 22.sp,
    ),
    val baseBold: TextStyle = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Bold,
        fontSize = 16.sp,
        lineHeight = 22.sp,
    ),
    val smallRegular: TextStyle = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Normal,
        fontSize = 14.sp,
        lineHeight = 20.sp,
    ),
    val smallBold: TextStyle = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Bold,
        fontSize = 14.sp,
        lineHeight = 20.sp,
    ),
    val captionRegular: TextStyle = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Medium,
        fontSize = 12.sp,
        lineHeight = 18.sp,
    ),
    val captionBold: TextStyle = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Bold,
        fontSize = 12.sp,
        lineHeight = 18.sp,
    ),
)

@Composable
fun rememberGasGuruTypography(): GasGuruTypography {
    val interFamily = rememberInterFamily()
    return GasGuruTypography(
        h1 = TextStyle(fontFamily = interFamily, fontWeight = FontWeight.Bold, fontSize = 36.sp, lineHeight = 44.sp),
        h2 = TextStyle(fontFamily = interFamily, fontWeight = FontWeight.Bold, fontSize = 32.sp, lineHeight = 40.sp),
        h3 = TextStyle(fontFamily = interFamily, fontWeight = FontWeight.Bold, fontSize = 28.sp, lineHeight = 36.sp),
        h4 = TextStyle(fontFamily = interFamily, fontWeight = FontWeight.Bold, fontSize = 24.sp, lineHeight = 32.sp),
        h5 = TextStyle(fontFamily = interFamily, fontWeight = FontWeight.Bold, fontSize = 20.sp, lineHeight = 28.sp),
        h6 = TextStyle(fontFamily = interFamily, fontWeight = FontWeight.Bold, fontSize = 18.sp, lineHeight = 24.sp),
        displayRegular = TextStyle(fontFamily = interFamily, fontWeight = FontWeight.Normal, fontSize = 48.sp, lineHeight = 58.sp),
        displayBold = TextStyle(fontFamily = interFamily, fontWeight = FontWeight.Bold, fontSize = 48.sp, lineHeight = 58.sp),
        baseRegular = TextStyle(fontFamily = interFamily, fontWeight = FontWeight.Normal, fontSize = 16.sp, lineHeight = 22.sp),
        baseBold = TextStyle(fontFamily = interFamily, fontWeight = FontWeight.Bold, fontSize = 16.sp, lineHeight = 22.sp),
        smallRegular = TextStyle(fontFamily = interFamily, fontWeight = FontWeight.Normal, fontSize = 14.sp, lineHeight = 20.sp),
        smallBold = TextStyle(fontFamily = interFamily, fontWeight = FontWeight.Bold, fontSize = 14.sp, lineHeight = 20.sp),
        captionRegular = TextStyle(fontFamily = interFamily, fontWeight = FontWeight.Medium, fontSize = 12.sp, lineHeight = 18.sp),
        captionBold = TextStyle(fontFamily = interFamily, fontWeight = FontWeight.Bold, fontSize = 12.sp, lineHeight = 18.sp),
    )
}

internal val LocalGasGuruTypography = staticCompositionLocalOf { GasGuruTypography() }
