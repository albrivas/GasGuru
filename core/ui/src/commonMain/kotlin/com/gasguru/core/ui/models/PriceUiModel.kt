package com.gasguru.core.ui.models

import androidx.compose.runtime.Composable
import com.gasguru.core.model.data.FuelType
import com.gasguru.core.ui.mapper.toUiModel
import kotlin.math.roundToLong
import org.jetbrains.compose.resources.stringResource

data class PriceUiModel(
    val rawPrice: Double,
    val fuelType: FuelType,
) {
    val hasPrice: Boolean get() = rawPrice > 0.0

    val formattedPrice: String
        get() = if (hasPrice) {
            val rounded = (rawPrice * 1000).roundToLong()
            val intPart = rounded / 1000
            val fracPart = (rounded % 1000).toString().padStart(3, '0')
            "$intPart.$fracPart €/l"
        } else {
            "0.000"
        }

    @Composable
    fun getDisplayPrice(): String = if (hasPrice) {
        formattedPrice
    } else {
        stringResource(resource = fuelType.toUiModel().noPriceRes)
    }
}
