package com.gasguru.core.ui.models

import androidx.annotation.StringRes
import com.gasguru.core.model.data.FuelType
import com.gasguru.core.ui.R
import com.gasguru.core.uikit.components.icon.FuelTypeIcons
import org.jetbrains.compose.resources.DrawableResource

data class FuelTypeUiModel(
    val type: FuelType,
    @StringRes val translationRes: Int,
    val iconRes: DrawableResource,
    @StringRes val noPriceRes: Int
) {
    companion object {
        val ALL_FUELS = listOf(
            FuelTypeUiModel(
                type = FuelType.GASOLINE_95,
                translationRes = R.string.gasoline_95,
                iconRes = FuelTypeIcons.Gasoline95,
                noPriceRes = R.string.sin_sp95
            ),
            FuelTypeUiModel(
                type = FuelType.GASOLINE_98,
                translationRes = R.string.gasoline_98,
                iconRes = FuelTypeIcons.Gasoline98,
                noPriceRes = R.string.sin_sp98
            ),
            FuelTypeUiModel(
                type = FuelType.DIESEL,
                translationRes = R.string.diesel,
                iconRes = FuelTypeIcons.Diesel,
                noPriceRes = R.string.sin_gasoleo_a
            ),
            FuelTypeUiModel(
                type = FuelType.DIESEL_PLUS,
                translationRes = R.string.diesel_plus,
                iconRes = FuelTypeIcons.DieselPlus,
                noPriceRes = R.string.sin_gasoleo_premium
            ),
            FuelTypeUiModel(
                type = FuelType.GASOLINE_95_PREMIUM,
                translationRes = R.string.gasoline_95_premium,
                iconRes = FuelTypeIcons.Gasoline95Premium,
                noPriceRes = R.string.sin_sp95_premium
            ),
            FuelTypeUiModel(
                type = FuelType.GASOLINE_95_E10,
                translationRes = R.string.gasoline_95_e10,
                iconRes = FuelTypeIcons.Gasoline95E10,
                noPriceRes = R.string.sin_sp95_e10
            ),
            FuelTypeUiModel(
                type = FuelType.GASOLINE_98_PREMIUM,
                translationRes = R.string.gasoline_98_premium,
                iconRes = FuelTypeIcons.Gasoline98Premium,
                noPriceRes = R.string.sin_sp98_premium
            ),
            FuelTypeUiModel(
                type = FuelType.GASOIL_B,
                translationRes = R.string.gasoil_b,
                iconRes = FuelTypeIcons.GasoilB,
                noPriceRes = R.string.sin_gasoleo_b
            ),
            FuelTypeUiModel(
                type = FuelType.ADBLUE,
                translationRes = R.string.adblue,
                iconRes = FuelTypeIcons.Adblue,
                noPriceRes = R.string.sin_adblue
            ),
        )
    }
}
