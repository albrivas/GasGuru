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

// Matches GasGuruTypography.baseRegular (16sp Normal) — station name
internal val WidgetStyleBodyMedium = TextStyle(
    fontWeight = FontWeight.Normal,
    fontSize = 16.sp,
)

// Matches GasGuruTypography.smallRegular (14sp Normal) — address / secondary text
internal val WidgetStyleCaption = TextStyle(
    fontWeight = FontWeight.Normal,
    fontSize = 14.sp,
)

// Matches GasGuruTypography.baseRegular (16sp Normal) — price, same as StatusChip
internal val WidgetStylePrice = TextStyle(
    fontWeight = FontWeight.Normal,
    fontSize = 16.sp,
)
