package com.gasguru.core.ui.models

import com.gasguru.core.model.data.FuelType
import com.gasguru.core.ui.generated.resources.Res
import com.gasguru.core.ui.generated.resources.adblue
import com.gasguru.core.ui.generated.resources.diesel
import com.gasguru.core.ui.generated.resources.diesel_plus
import com.gasguru.core.ui.generated.resources.gasoil_b
import com.gasguru.core.ui.generated.resources.gasoline_95
import com.gasguru.core.ui.generated.resources.gasoline_95_e10
import com.gasguru.core.ui.generated.resources.gasoline_95_premium
import com.gasguru.core.ui.generated.resources.gasoline_98
import com.gasguru.core.ui.generated.resources.gasoline_98_premium
import com.gasguru.core.ui.generated.resources.sin_adblue
import com.gasguru.core.ui.generated.resources.sin_gasoleo_a
import com.gasguru.core.ui.generated.resources.sin_gasoleo_b
import com.gasguru.core.ui.generated.resources.sin_gasoleo_premium
import com.gasguru.core.ui.generated.resources.sin_sp95
import com.gasguru.core.ui.generated.resources.sin_sp95_e10
import com.gasguru.core.ui.generated.resources.sin_sp95_premium
import com.gasguru.core.ui.generated.resources.sin_sp98
import com.gasguru.core.ui.generated.resources.sin_sp98_premium
import com.gasguru.core.uikit.components.icon.FuelTypeIcons
import org.jetbrains.compose.resources.DrawableResource
import org.jetbrains.compose.resources.StringResource

data class FuelTypeUiModel(
    val type: FuelType,
    val translationRes: StringResource,
    val iconRes: DrawableResource,
    val noPriceRes: StringResource,
) {
    companion object {
        val ALL_FUELS = listOf(
            FuelTypeUiModel(
                type = FuelType.GASOLINE_95,
                translationRes = Res.string.gasoline_95,
                iconRes = FuelTypeIcons.Gasoline95,
                noPriceRes = Res.string.sin_sp95,
            ),
            FuelTypeUiModel(
                type = FuelType.GASOLINE_98,
                translationRes = Res.string.gasoline_98,
                iconRes = FuelTypeIcons.Gasoline98,
                noPriceRes = Res.string.sin_sp98,
            ),
            FuelTypeUiModel(
                type = FuelType.DIESEL,
                translationRes = Res.string.diesel,
                iconRes = FuelTypeIcons.Diesel,
                noPriceRes = Res.string.sin_gasoleo_a,
            ),
            FuelTypeUiModel(
                type = FuelType.DIESEL_PLUS,
                translationRes = Res.string.diesel_plus,
                iconRes = FuelTypeIcons.DieselPlus,
                noPriceRes = Res.string.sin_gasoleo_premium,
            ),
            FuelTypeUiModel(
                type = FuelType.GASOLINE_95_PREMIUM,
                translationRes = Res.string.gasoline_95_premium,
                iconRes = FuelTypeIcons.Gasoline95Premium,
                noPriceRes = Res.string.sin_sp95_premium,
            ),
            FuelTypeUiModel(
                type = FuelType.GASOLINE_95_E10,
                translationRes = Res.string.gasoline_95_e10,
                iconRes = FuelTypeIcons.Gasoline95E10,
                noPriceRes = Res.string.sin_sp95_e10,
            ),
            FuelTypeUiModel(
                type = FuelType.GASOLINE_98_PREMIUM,
                translationRes = Res.string.gasoline_98_premium,
                iconRes = FuelTypeIcons.Gasoline98Premium,
                noPriceRes = Res.string.sin_sp98_premium,
            ),
            FuelTypeUiModel(
                type = FuelType.GASOIL_B,
                translationRes = Res.string.gasoil_b,
                iconRes = FuelTypeIcons.GasoilB,
                noPriceRes = Res.string.sin_gasoleo_b,
            ),
            FuelTypeUiModel(
                type = FuelType.ADBLUE,
                translationRes = Res.string.adblue,
                iconRes = FuelTypeIcons.Adblue,
                noPriceRes = Res.string.sin_adblue,
            ),
        )
    }
}
