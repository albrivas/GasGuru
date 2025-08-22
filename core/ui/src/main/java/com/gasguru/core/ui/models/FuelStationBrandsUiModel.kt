package com.gasguru.core.ui.models

import androidx.annotation.DrawableRes
import com.gasguru.core.model.data.FuelStationBrandsType
import com.gasguru.core.uikit.components.icon.FuelStationIcons

data class FuelStationBrandsUiModel(
    val type: FuelStationBrandsType,
    val name: String,
    @DrawableRes val iconRes: Int
) {
    companion object {
        val ALL_BRANDS = listOf(
            FuelStationBrandsUiModel(
                type = FuelStationBrandsType.ALCAMPO,
                name = FuelStationBrandsType.ALCAMPO.value,
                iconRes = FuelStationIcons.Alcampo
            ),
            FuelStationBrandsUiModel(
                type = FuelStationBrandsType.BALLENOIL,
                name = FuelStationBrandsType.BALLENOIL.value,
                iconRes = FuelStationIcons.Ballenoil
            ),
            FuelStationBrandsUiModel(
                type = FuelStationBrandsType.BONAREA,
                name = FuelStationBrandsType.BONAREA.value,
                iconRes = FuelStationIcons.Bonarea
            ),
            FuelStationBrandsUiModel(
                type = FuelStationBrandsType.BP,
                name = FuelStationBrandsType.BP.value,
                iconRes = FuelStationIcons.Bp
            ),
            FuelStationBrandsUiModel(
                type = FuelStationBrandsType.CARREFOUR,
                name = FuelStationBrandsType.CARREFOUR.value,
                iconRes = FuelStationIcons.Carrefour
            ),
            FuelStationBrandsUiModel(
                type = FuelStationBrandsType.CEPSA,
                name = FuelStationBrandsType.CEPSA.value,
                iconRes = FuelStationIcons.Cepsa
            ),
            FuelStationBrandsUiModel(
                type = FuelStationBrandsType.DISA,
                name = FuelStationBrandsType.DISA.value,
                iconRes = FuelStationIcons.Disa
            ),
            FuelStationBrandsUiModel(
                type = FuelStationBrandsType.ECLERC,
                name = FuelStationBrandsType.ECLERC.value,
                iconRes = FuelStationIcons.Eclerc
            ),
            FuelStationBrandsUiModel(
                type = FuelStationBrandsType.ELECLERC,
                name = FuelStationBrandsType.ELECLERC.value,
                iconRes = FuelStationIcons.Eleclerc
            ),
            FuelStationBrandsUiModel(
                type = FuelStationBrandsType.EROSKI,
                name = FuelStationBrandsType.EROSKI.value,
                iconRes = FuelStationIcons.Eroski
            ),
            FuelStationBrandsUiModel(
                type = FuelStationBrandsType.ESSO,
                name = FuelStationBrandsType.ESSO.value,
                iconRes = FuelStationIcons.Esso
            ),
            FuelStationBrandsUiModel(
                type = FuelStationBrandsType.GALP,
                name = FuelStationBrandsType.GALP.value,
                iconRes = FuelStationIcons.Galp
            ),
            FuelStationBrandsUiModel(
                type = FuelStationBrandsType.MAKRO,
                name = FuelStationBrandsType.MAKRO.value,
                iconRes = FuelStationIcons.Makro
            ),
            FuelStationBrandsUiModel(
                type = FuelStationBrandsType.MEROIL,
                name = FuelStationBrandsType.MEROIL.value,
                iconRes = FuelStationIcons.Meroil
            ),
            FuelStationBrandsUiModel(
                type = FuelStationBrandsType.PETRONOR,
                name = FuelStationBrandsType.PETRONOR.value,
                iconRes = FuelStationIcons.Petronor
            ),
            FuelStationBrandsUiModel(
                type = FuelStationBrandsType.REPSOL,
                name = FuelStationBrandsType.REPSOL.value,
                iconRes = FuelStationIcons.Repsol
            ),
            FuelStationBrandsUiModel(
                type = FuelStationBrandsType.SHELL,
                name = FuelStationBrandsType.SHELL.value,
                iconRes = FuelStationIcons.Shell
            ),
            FuelStationBrandsUiModel(
                type = FuelStationBrandsType.TEXACO,
                name = FuelStationBrandsType.TEXACO.value,
                iconRes = FuelStationIcons.Texaco
            ),
            FuelStationBrandsUiModel(
                type = FuelStationBrandsType.TGAS,
                name = FuelStationBrandsType.TGAS.value,
                iconRes = FuelStationIcons.Tgas
            ),
            FuelStationBrandsUiModel(
                type = FuelStationBrandsType.ZOLOIL,
                name = FuelStationBrandsType.ZOLOIL.value,
                iconRes = FuelStationIcons.Tgas
            ),
            FuelStationBrandsUiModel(
                type = FuelStationBrandsType.PC,
                name = FuelStationBrandsType.PC.value,
                iconRes = FuelStationIcons.Pcan
            ),
            FuelStationBrandsUiModel(
                type = FuelStationBrandsType.Q8,
                name = FuelStationBrandsType.Q8.value,
                iconRes = FuelStationIcons.Q8
            ),
            FuelStationBrandsUiModel(
                type = FuelStationBrandsType.SILVER_FUEL,
                name = FuelStationBrandsType.SILVER_FUEL.value,
                iconRes = FuelStationIcons.SilverFuel
            ),
            FuelStationBrandsUiModel(
                type = FuelStationBrandsType.AZUL_OIL,
                name = FuelStationBrandsType.AZUL_OIL.value,
                iconRes = FuelStationIcons.AzulOil
            ),
            FuelStationBrandsUiModel(
                type = FuelStationBrandsType.FARRUCO,
                name = FuelStationBrandsType.FARRUCO.value,
                iconRes = FuelStationIcons.Farruco
            ),
            FuelStationBrandsUiModel(
                type = FuelStationBrandsType.REPOSTAR,
                name = FuelStationBrandsType.REPOSTAR.value,
                iconRes = FuelStationIcons.Repostar
            ),
            FuelStationBrandsUiModel(
                type = FuelStationBrandsType.UNKNOWN,
                name = FuelStationBrandsType.UNKNOWN.value,
                iconRes = FuelStationIcons.Uknown
            ),
            FuelStationBrandsUiModel(
                type = FuelStationBrandsType.CAMPSA,
                name = FuelStationBrandsType.CAMPSA.value,
                iconRes = FuelStationIcons.Campsa
            ),
            FuelStationBrandsUiModel(
                type = FuelStationBrandsType.AUTONETOIL,
                name = FuelStationBrandsType.AUTONETOIL.value,
                iconRes = FuelStationIcons.Autonetoil
            ),
            FuelStationBrandsUiModel(
                type = FuelStationBrandsType.PETROPRIX,
                name = FuelStationBrandsType.PETROPRIX.value,
                iconRes = FuelStationIcons.Petroprix
            ),
            FuelStationBrandsUiModel(
                type = FuelStationBrandsType.ECONOIL,
                name = FuelStationBrandsType.ECONOIL.value,
                iconRes = FuelStationIcons.Econoil
            ),
            FuelStationBrandsUiModel(
                type = FuelStationBrandsType.FISCOGAS,
                name = FuelStationBrandsType.FISCOGAS.value,
                iconRes = FuelStationIcons.Fiscogas
            ),
            FuelStationBrandsUiModel(
                type = FuelStationBrandsType.ENERGY_CARBURANTES,
                name = FuelStationBrandsType.ENERGY_CARBURANTES.value,
                iconRes = FuelStationIcons.EnergyCarburantes
            ),
            FuelStationBrandsUiModel(
                type = FuelStationBrandsType.AVIA,
                name = FuelStationBrandsType.AVIA.value,
                iconRes = FuelStationIcons.Avia
            ),
            FuelStationBrandsUiModel(
                type = FuelStationBrandsType.GM_FUEL,
                name = FuelStationBrandsType.GM_FUEL.value,
                iconRes = FuelStationIcons.GmFuel
            ),
            FuelStationBrandsUiModel(
                type = FuelStationBrandsType.MOEVE,
                name = FuelStationBrandsType.MOEVE.value,
                iconRes = FuelStationIcons.Moeve
            ),
            FuelStationBrandsUiModel(
                type = FuelStationBrandsType.VALCARCE,
                name = FuelStationBrandsType.VALCARCE.value,
                iconRes = FuelStationIcons.Valcarce
            )
        )

        fun fromBrandType(brandType: FuelStationBrandsType): FuelStationBrandsUiModel =
            ALL_BRANDS.first { it.type == brandType }
    }
}
