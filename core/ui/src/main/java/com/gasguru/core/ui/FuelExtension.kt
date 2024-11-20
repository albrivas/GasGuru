package com.gasguru.core.ui

import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import com.gasguru.core.model.data.FuelStation
import com.gasguru.core.model.data.FuelStationBrandsType
import com.gasguru.core.model.data.FuelType
import com.gasguru.core.model.data.PriceCategory
import com.gasguru.core.uikit.components.price.PriceItemModel
import com.gasguru.core.uikit.icon.FuelStationIcons
import com.gasguru.core.uikit.theme.AccentGreen
import com.gasguru.core.uikit.theme.AccentOrange
import com.gasguru.core.uikit.theme.AccentRed
import com.gasguru.core.uikit.theme.secondaryLight
import java.text.DecimalFormat
import com.gasguru.core.uikit.R as RUikit

fun FuelType.translation() = when (this) {
    FuelType.GASOLINE_95 -> R.string.gasoline_95
    FuelType.GASOLINE_98 -> R.string.gasoline_98
    FuelType.DIESEL -> R.string.diesel
    FuelType.DIESEL_PLUS -> R.string.diesel_plus
    FuelType.GASOLINE_95_PREMIUM -> R.string.gasoline_95_premium
    FuelType.GASOLINE_95_E10 -> R.string.gasoline_95_e10
    FuelType.GASOLINE_98_PREMIUM -> R.string.gasoline_98_premium
    FuelType.GASOIL_B -> R.string.gasoil_b
}

fun Int.toFuelType() = when (this) {
    R.string.gasoline_95 -> FuelType.GASOLINE_95
    R.string.gasoline_98 -> FuelType.GASOLINE_98
    R.string.diesel -> FuelType.DIESEL
    R.string.diesel_plus -> FuelType.DIESEL_PLUS
    R.string.gasoline_95_premium -> FuelType.GASOLINE_95_PREMIUM
    R.string.gasoline_95_e10 -> FuelType.GASOLINE_95_E10
    R.string.gasoline_98_premium -> FuelType.GASOLINE_98_PREMIUM
    R.string.gasoil_b -> FuelType.GASOIL_B
    else -> FuelType.GASOLINE_95
}

fun FuelStationBrandsType.toBrandStationIcon() = when (this) {
    FuelStationBrandsType.ALCAMPO -> FuelStationIcons.Alcampo
    FuelStationBrandsType.BALLENOIL -> FuelStationIcons.Ballenoil
    FuelStationBrandsType.BONAREA -> FuelStationIcons.Bonarea
    FuelStationBrandsType.BP -> FuelStationIcons.Bp
    FuelStationBrandsType.CARREFOUR -> FuelStationIcons.Carrefour
    FuelStationBrandsType.CEPSA -> FuelStationIcons.Cepsa
    FuelStationBrandsType.DISA -> FuelStationIcons.Disa
    FuelStationBrandsType.ECLERC -> FuelStationIcons.Eclerc
    FuelStationBrandsType.ELECLERC -> FuelStationIcons.Eleclerc
    FuelStationBrandsType.EROSKI -> FuelStationIcons.Eroski
    FuelStationBrandsType.ESSO -> FuelStationIcons.Esso
    FuelStationBrandsType.GALP -> FuelStationIcons.Galp
    FuelStationBrandsType.MAKRO -> FuelStationIcons.Makro
    FuelStationBrandsType.MEROIL -> FuelStationIcons.Meroil
    FuelStationBrandsType.PETRONOR -> FuelStationIcons.Petronor
    FuelStationBrandsType.REPSOL -> FuelStationIcons.Repsol
    FuelStationBrandsType.SHELL -> FuelStationIcons.Shell
    FuelStationBrandsType.TEXACO -> FuelStationIcons.Texaco
    FuelStationBrandsType.TGAS -> FuelStationIcons.Tgas
    FuelStationBrandsType.ZOLOIL -> FuelStationIcons.Tgas
    FuelStationBrandsType.PC -> FuelStationIcons.Pcan
    FuelStationBrandsType.Q8 -> FuelStationIcons.Q8
    FuelStationBrandsType.SILVER_FUEL -> FuelStationIcons.SilverFuel
    FuelStationBrandsType.AZUL_OIL -> FuelStationIcons.AzulOil
    FuelStationBrandsType.FARRUCO -> FuelStationIcons.Farruco
    FuelStationBrandsType.REPOSTAR -> FuelStationIcons.Repostar
    FuelStationBrandsType.UNKNOWN -> FuelStationIcons.Uknown
    FuelStationBrandsType.CAMPSA -> FuelStationIcons.Campsa
    FuelStationBrandsType.AUTONETOIL -> FuelStationIcons.Autonetoil
    FuelStationBrandsType.PETROPRIX -> FuelStationIcons.Petroprix
    FuelStationBrandsType.ECONOIL -> FuelStationIcons.Econoil
    FuelStationBrandsType.FISCOGAS -> FuelStationIcons.Fiscogas
    FuelStationBrandsType.ENERGY_CARBURANTES -> FuelStationIcons.EnergyCarburantes
    FuelStationBrandsType.AVIA -> FuelStationIcons.Avia
    FuelStationBrandsType.GM_FUEL -> FuelStationIcons.GmFuel
    FuelStationBrandsType.VALCARCE -> FuelStationIcons.Valcarce
}

fun PriceCategory.toColor() = when (this) {
    PriceCategory.NONE -> secondaryLight
    PriceCategory.CHEAP -> AccentGreen
    PriceCategory.NORMAL -> AccentOrange
    PriceCategory.EXPENSIVE -> AccentRed
}

fun FuelType?.getPrice(fuelStation: FuelStation): String {
    val decimalFormat = DecimalFormat("#.000")
    return when (this) {
        FuelType.GASOLINE_95 -> decimalFormat.format(fuelStation.priceGasoline95E5)
        FuelType.GASOLINE_98 -> decimalFormat.format(fuelStation.priceGasoline98E5)
        FuelType.DIESEL -> decimalFormat.format(fuelStation.priceGasoilA)
        FuelType.DIESEL_PLUS -> decimalFormat.format(fuelStation.priceGasoilPremium)
        FuelType.GASOLINE_95_PREMIUM -> decimalFormat.format(fuelStation.priceGasoline95E5Premium)
        FuelType.GASOLINE_95_E10 -> decimalFormat.format(fuelStation.priceGasoline95E10)
        FuelType.GASOLINE_98_PREMIUM -> decimalFormat.format(fuelStation.priceGasoline98E10)
        FuelType.GASOIL_B -> decimalFormat.format(fuelStation.priceGasoilB)
        null -> "0.000"
    }
}

fun FuelType.getIcon() = when (this) {
    FuelType.GASOLINE_95 -> RUikit.drawable.ic_gasoline_95
    FuelType.GASOLINE_98 -> RUikit.drawable.ic_gasoline_98
    FuelType.DIESEL -> RUikit.drawable.ic_diesel
    FuelType.DIESEL_PLUS -> RUikit.drawable.ic_diesel_plus
    FuelType.GASOLINE_95_PREMIUM -> RUikit.drawable.ic_gasoline_95_premium
    FuelType.GASOLINE_95_E10 -> RUikit.drawable.ic_gasoline_95_e10
    FuelType.GASOLINE_98_PREMIUM -> RUikit.drawable.ic_gasoline_98_premium
    FuelType.GASOIL_B -> RUikit.drawable.ic_gasoleo_b
}

@Composable
fun FuelStation.getFuelPriceItems(): List<PriceItemModel> {
    return listOf(
        PriceItemModel(
            icon = RUikit.drawable.ic_diesel,
            fuelName = stringResource(id = R.string.diesel),
            price = "$priceGasoilA €/L"
        ),
        PriceItemModel(
            icon = RUikit.drawable.ic_diesel_plus,
            fuelName = stringResource(id = R.string.diesel_plus),
            price = "$priceGasoilPremium €/L"
        ),
        PriceItemModel(
            icon = RUikit.drawable.ic_gasoline_95,
            fuelName = stringResource(id = R.string.gasoline_95),
            price = "$priceGasoline95E5 €/L"
        ),
        PriceItemModel(
            icon = RUikit.drawable.ic_gasoline_98,
            fuelName = stringResource(id = R.string.gasoline_98),
            price = "$priceGasoline98E5 €/L"
        ),
        PriceItemModel(
            icon = RUikit.drawable.ic_gasoline_95_e10,
            fuelName = stringResource(id = R.string.gasoline_95_e10),
            price = "$priceGasoline95E10 €/L"
        ),
        PriceItemModel(
            icon = RUikit.drawable.ic_gasoline_98_premium,
            fuelName = stringResource(id = R.string.gasoline_98_premium),
            price = "$priceGasoline98E10 €/L"
        ),
        PriceItemModel(
            icon = RUikit.drawable.ic_gasoline_95_premium,
            fuelName = stringResource(id = R.string.gasoline_95_premium),
            price = "$priceGasoline95E5Premium €/L"
        ),
        PriceItemModel(
            icon = RUikit.drawable.ic_gasoleo_b,
            fuelName = stringResource(id = R.string.gasoil_b),
            price = "$priceGasoilB €/L"
        )

    ).filter { it.price > "0.0 €/L" }
}
