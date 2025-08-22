package com.gasguru.core.ui

import android.content.Context
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import com.gasguru.core.model.data.FuelStation
import com.gasguru.core.model.data.FuelType
import com.gasguru.core.model.data.PriceCategory
import com.gasguru.core.ui.models.FuelStationUiModel
import com.gasguru.core.ui.models.FuelTypeUiModel
import com.gasguru.core.uikit.components.price.PriceItemModel
import com.gasguru.core.uikit.theme.GasGuruTheme
import java.text.DecimalFormat

fun Int.toFuelType(): FuelType =
    FuelTypeUiModel.ALL_FUELS.firstOrNull { it.translationRes == this }?.type
        ?: FuelType.GASOLINE_95

fun FuelType?.getPrice(context: Context, fuelStation: FuelStation): String {
    val decimalFormat = DecimalFormat("0.000")
    return when (this) {
        null -> "0.000"
        else -> {
            val price = getFuelPrice(this, fuelStation)
            if (price == 0.0) {
                context.getString(FuelTypeUiModel.fromFuelType(this).noPriceRes)
            } else {
                "${decimalFormat.format(price)} €/l"
            }
        }
    }
}

private fun getFuelPrice(fuelType: FuelType, fuelStation: FuelStation): Double {
    return fuelType.extractPrice(fuelStation)
}

@Composable
fun PriceCategory.toColor() = when (this) {
    PriceCategory.NONE -> GasGuruTheme.colors.secondaryLight
    PriceCategory.CHEAP -> GasGuruTheme.colors.accentGreen
    PriceCategory.NORMAL -> GasGuruTheme.colors.accentOrange
    PriceCategory.EXPENSIVE -> GasGuruTheme.colors.accentRed
}

fun FuelStation.toUiModel(): FuelStationUiModel = FuelStationUiModel.from(this)

@Composable
fun FuelStation.getFuelPriceItems(): List<PriceItemModel> {
    return FuelTypeUiModel.ALL_FUELS.map { fuelUiModel ->
        val price = getFuelPrice(fuelUiModel.type, this)
        PriceItemModel(
            icon = fuelUiModel.iconRes,
            fuelName = stringResource(id = fuelUiModel.translationRes),
            price = "$price €/L"
        )
    }.filter { it.price > "0.0 €/L" }
}
