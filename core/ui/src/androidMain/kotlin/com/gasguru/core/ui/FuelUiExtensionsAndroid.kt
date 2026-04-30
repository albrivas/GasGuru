package com.gasguru.core.ui

import android.content.Context
import com.gasguru.core.model.data.FuelStation
import com.gasguru.core.model.data.FuelType
import com.gasguru.core.ui.mapper.toPriceUiModel
import com.gasguru.core.ui.mapper.toUiModel
import com.gasguru.core.ui.models.FuelTypeUiModel
import kotlinx.coroutines.runBlocking
import org.jetbrains.compose.resources.getString

fun FuelType?.getPrice(context: Context, fuelStation: FuelStation): String = when (this) {
    null -> "0.000"
    else -> {
        val priceModel = toPriceUiModel(fuelStation = fuelStation)
        if (priceModel.hasPrice) {
            priceModel.formattedPrice
        } else {
            runBlocking { getString(toUiModel().noPriceRes) }
        }
    }
}

fun String.toFuelType(context: Context): FuelType =
    FuelTypeUiModel.ALL_FUELS.firstOrNull {
        runBlocking { getString(it.translationRes) } == this
    }?.type ?: FuelType.GASOLINE_95
