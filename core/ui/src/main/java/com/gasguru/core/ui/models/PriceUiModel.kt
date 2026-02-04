package com.gasguru.core.ui.models

import android.content.Context
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import com.gasguru.core.model.data.FuelStation
import com.gasguru.core.model.data.FuelType
import com.gasguru.core.ui.mapper.toUiModel
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
            context.getString(fuelType.toUiModel().noPriceRes)
        }
    }

    @Composable
    fun getDisplayPrice(): String {
        return if (hasPrice) {
            formattedPrice
        } else {
            stringResource(id = fuelType.toUiModel().noPriceRes)
        }
    }
}
