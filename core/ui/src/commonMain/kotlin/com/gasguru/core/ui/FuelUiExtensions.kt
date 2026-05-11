package com.gasguru.core.ui

import androidx.compose.runtime.Composable
import com.gasguru.core.model.data.FuelStation
import com.gasguru.core.model.data.FuelType
import com.gasguru.core.model.data.PriceCategory
import com.gasguru.core.ui.mapper.toPriceUiModel
import com.gasguru.core.uikit.theme.GasGuruTheme

@Composable
fun FuelType?.getPrice(fuelStation: FuelStation): String = when (this) {
    null -> "0.000"
    else -> toPriceUiModel(fuelStation = fuelStation).getDisplayPrice()
}

@Composable
fun PriceCategory.toColor() = when (this) {
    PriceCategory.NONE -> GasGuruTheme.colors.secondaryLight
    PriceCategory.CHEAP -> GasGuruTheme.colors.accentGreen
    PriceCategory.NORMAL -> GasGuruTheme.colors.accentOrange
    PriceCategory.EXPENSIVE -> GasGuruTheme.colors.accentRed
}
