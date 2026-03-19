package com.gasguru.feature.widget.theme

import androidx.compose.ui.unit.sp
import androidx.glance.text.FontWeight
import androidx.glance.text.TextStyle

// Matches GasGuruTypography.baseBold (16sp Bold)
internal val WidgetStyleHeader = TextStyle(
    fontWeight = FontWeight.Bold,
    fontSize = 16.sp,
)

// Matches GasGuruTypography.smallBold (14sp Bold) — used in empty state title
internal val WidgetStyleEmptyTitle = TextStyle(
    fontWeight = FontWeight.Bold,
    fontSize = 14.sp,
)

// Matches GasGuruTypography.captionRegular (12sp) — used in empty state subtitle
internal val WidgetStyleEmptySubtitle = TextStyle(
    fontSize = 12.sp,
)

// Matches GasGuruTypography.smallBold at 13sp Medium — station name
internal val WidgetStyleBodyMedium = TextStyle(
    fontWeight = FontWeight.Medium,
    fontSize = 13.sp,
)

// Matches GasGuruTypography.captionRegular at 11sp — address / secondary text
internal val WidgetStyleCaption = TextStyle(
    fontSize = 11.sp,
)

// Matches GasGuruTypography.smallBold at 13sp Bold — price
internal val WidgetStylePrice = TextStyle(
    fontWeight = FontWeight.Bold,
    fontSize = 13.sp,
)
