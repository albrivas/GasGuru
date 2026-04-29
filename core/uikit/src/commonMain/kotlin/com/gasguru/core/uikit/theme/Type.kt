package com.gasguru.core.uikit.theme

import androidx.compose.material3.Typography
import androidx.compose.runtime.Composable
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.gasguru.core.uikit.generated.resources.Res
import com.gasguru.core.uikit.generated.resources.inter_black
import com.gasguru.core.uikit.generated.resources.inter_bold
import com.gasguru.core.uikit.generated.resources.inter_extra_bold
import com.gasguru.core.uikit.generated.resources.inter_extra_light
import com.gasguru.core.uikit.generated.resources.inter_light
import com.gasguru.core.uikit.generated.resources.inter_medium
import com.gasguru.core.uikit.generated.resources.inter_regular
import com.gasguru.core.uikit.generated.resources.inter_semi_bold
import com.gasguru.core.uikit.generated.resources.inter_thin
import org.jetbrains.compose.resources.Font

@Composable
fun rememberInterFamily() = FontFamily(
    Font(Res.font.inter_black, FontWeight.Black, FontStyle.Normal),
    Font(Res.font.inter_bold, FontWeight.Bold, FontStyle.Normal),
    Font(Res.font.inter_extra_bold, FontWeight.ExtraBold, FontStyle.Normal),
    Font(Res.font.inter_light, FontWeight.Light, FontStyle.Normal),
    Font(Res.font.inter_medium, FontWeight.Medium, FontStyle.Normal),
    Font(Res.font.inter_extra_light, FontWeight.ExtraLight, FontStyle.Normal),
    Font(Res.font.inter_regular, FontWeight.Normal, FontStyle.Normal),
    Font(Res.font.inter_semi_bold, FontWeight.SemiBold, FontStyle.Normal),
    Font(Res.font.inter_thin, FontWeight.Thin, FontStyle.Normal),
)

@Composable
fun rememberTypography(): Typography {
    val interFamily = rememberInterFamily()
    return Typography(
        bodyLarge = TextStyle(
            fontFamily = interFamily,
            fontWeight = FontWeight.Bold,
            fontSize = 16.sp,
            lineHeight = 24.sp,
            letterSpacing = 0.5.sp,
        ),
        bodyMedium = TextStyle(
            fontFamily = interFamily,
            fontWeight = FontWeight.Medium,
            fontSize = 16.sp,
            lineHeight = 24.sp,
            letterSpacing = 0.5.sp,
        ),
        bodySmall = TextStyle(
            fontFamily = interFamily,
            fontWeight = FontWeight.Normal,
            fontSize = 14.sp,
            lineHeight = 20.sp,
            letterSpacing = 0.25.sp,
        ),
        titleLarge = TextStyle(
            fontFamily = interFamily,
            fontWeight = FontWeight.ExtraBold,
            fontSize = 32.sp,
            lineHeight = 24.sp,
            letterSpacing = 0.5.sp,
        ),
        titleMedium = TextStyle(
            fontFamily = interFamily,
            fontWeight = FontWeight.SemiBold,
            fontSize = 18.sp,
            lineHeight = 24.sp,
            letterSpacing = 0.5.sp,
        ),
        displayMedium = TextStyle(
            fontFamily = interFamily,
            fontWeight = FontWeight.Light,
            fontSize = 16.sp,
            lineHeight = 24.sp,
            letterSpacing = 0.5.sp,
        ),
        displaySmall = TextStyle(
            fontFamily = interFamily,
            fontWeight = FontWeight.Light,
            fontSize = 12.sp,
            lineHeight = 24.sp,
            letterSpacing = 0.5.sp,
        ),
        titleSmall = TextStyle(
            fontFamily = interFamily,
            fontWeight = FontWeight.Bold,
            fontSize = 14.sp,
            lineHeight = 20.sp,
            letterSpacing = 0.25.sp,
        ),
        displayLarge = TextStyle(
            fontFamily = interFamily,
            fontWeight = FontWeight.Light,
            fontSize = 24.sp,
            lineHeight = 32.sp,
            letterSpacing = 0.5.sp,
        ),
        labelSmall = TextStyle(
            fontFamily = interFamily,
            fontWeight = FontWeight.SemiBold,
            fontSize = 12.sp,
            lineHeight = 18.sp,
            letterSpacing = 0.5.sp,
        ),
    )
}
