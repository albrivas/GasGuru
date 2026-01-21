package com.gasguru.core.ui.models

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import com.gasguru.core.model.data.FuelType
import com.gasguru.core.ui.R
import com.gasguru.core.uikit.R as RUikit

data class FuelTypeUiModel(
    val type: FuelType,
    @StringRes val translationRes: Int,
    @DrawableRes val iconRes: Int,
    @StringRes val noPriceRes: Int
) {
    companion object {
        val ALL_FUELS = listOf(
            FuelTypeUiModel(
                type = FuelType.GASOLINE_95,
                translationRes = R.string.gasoline_95,
                iconRes = RUikit.drawable.ic_gasoline_95,
                noPriceRes = R.string.sin_sp95
            ),
            FuelTypeUiModel(
                type = FuelType.GASOLINE_98,
                translationRes = R.string.gasoline_98,
                iconRes = RUikit.drawable.ic_gasoline_98,
                noPriceRes = R.string.sin_sp98
            ),
            FuelTypeUiModel(
                type = FuelType.DIESEL,
                translationRes = R.string.diesel,
                iconRes = RUikit.drawable.ic_diesel,
                noPriceRes = R.string.sin_gasoleo_a
            ),
            FuelTypeUiModel(
                type = FuelType.DIESEL_PLUS,
                translationRes = R.string.diesel_plus,
                iconRes = RUikit.drawable.ic_diesel_plus,
                noPriceRes = R.string.sin_gasoleo_premium
            ),
            FuelTypeUiModel(
                type = FuelType.GASOLINE_95_PREMIUM,
                translationRes = R.string.gasoline_95_premium,
                iconRes = RUikit.drawable.ic_gasoline_95_premium,
                noPriceRes = R.string.sin_sp95_premium
            ),
            FuelTypeUiModel(
                type = FuelType.GASOLINE_95_E10,
                translationRes = R.string.gasoline_95_e10,
                iconRes = RUikit.drawable.ic_gasoline_95_e10,
                noPriceRes = R.string.sin_sp95_e10
            ),
            FuelTypeUiModel(
                type = FuelType.GASOLINE_98_PREMIUM,
                translationRes = R.string.gasoline_98_premium,
                iconRes = RUikit.drawable.ic_gasoline_98_premium,
                noPriceRes = R.string.sin_sp98_premium
            ),
            FuelTypeUiModel(
                type = FuelType.GASOIL_B,
                translationRes = R.string.gasoil_b,
                iconRes = RUikit.drawable.ic_gasoleo_b,
                noPriceRes = R.string.sin_gasoleo_b
            ),
            FuelTypeUiModel(
                type = FuelType.ADBLUE,
                translationRes = R.string.adblue,
                iconRes = RUikit.drawable.ic_adblue,
                noPriceRes = R.string.sin_adblue
            ),
        )

        fun fromFuelType(fuelType: FuelType): FuelTypeUiModel =
            ALL_FUELS.first { it.type == fuelType }
    }
}
