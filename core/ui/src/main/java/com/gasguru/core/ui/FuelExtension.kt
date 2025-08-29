package com.gasguru.core.ui

import android.content.Context
import androidx.compose.runtime.Composable
import com.gasguru.core.model.data.FuelStation
import com.gasguru.core.model.data.FuelType
import com.gasguru.core.model.data.PriceCategory
import com.gasguru.core.ui.models.FuelStationUiModel
import com.gasguru.core.ui.models.FuelTypeUiModel
import com.gasguru.core.ui.models.PriceUiModel
import com.gasguru.core.uikit.theme.GasGuruTheme

fun Int.toFuelType(): FuelType =
    FuelTypeUiModel.ALL_FUELS.firstOrNull { it.translationRes == this }?.type
        ?: FuelType.GASOLINE_95

fun FuelType?.getPrice(context: Context, fuelStation: FuelStation): String {
    return when (this) {
        null -> "0.000"
        else -> PriceUiModel.from(this, fuelStation).getDisplayPrice(context)
    }
}

@Composable
fun FuelType?.getPrice(fuelStation: FuelStation): String {
    return when (this) {
        null -> "0.000"
        else -> PriceUiModel.from(this, fuelStation).getDisplayPrice()
    }
}

@Composable
fun PriceCategory.toColor() = when (this) {
    PriceCategory.NONE -> GasGuruTheme.colors.secondaryLight
    PriceCategory.CHEAP -> GasGuruTheme.colors.accentGreen
    PriceCategory.NORMAL -> GasGuruTheme.colors.accentOrange
    PriceCategory.EXPENSIVE -> GasGuruTheme.colors.accentRed
}

fun FuelStation.toUiModel(): FuelStationUiModel = FuelStationUiModel.from(this)
