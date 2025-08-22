package com.gasguru.core.ui.models

import android.content.Context
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import com.gasguru.core.model.data.FuelStation
import com.gasguru.core.model.data.FuelType
import java.text.DecimalFormat

data class PriceUiModel(
    val rawPrice: Double,
    val fuelType: FuelType
) {
    val hasPrice: Boolean get() = rawPrice > 0.0

    val formattedPrice: String
        get() = if (hasPrice) {
            val decimalFormat = DecimalFormat("0.000")
            "${decimalFormat.format(rawPrice)} €/l"
        } else {
            "0.000"
        }

    fun getDisplayPrice(context: Context): String {
        return if (hasPrice) {
            formattedPrice
        } else {
            context.getString(FuelTypeUiModel.fromFuelType(fuelType).noPriceRes)
        }
    }

    @Composable
    fun getDisplayPrice(): String {
        return if (hasPrice) {
            formattedPrice
        } else {
            stringResource(id = FuelTypeUiModel.fromFuelType(fuelType).noPriceRes)
        }
    }

    companion object {
        fun from(fuelType: FuelType, fuelStation: FuelStation): PriceUiModel {
            return PriceUiModel(
                rawPrice = fuelType.extractPrice(fuelStation),
                fuelType = fuelType
            )
        }
    }
}
