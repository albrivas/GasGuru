package com.albrivas.fuelpump.core.ui

import com.albrivas.fuelpump.core.model.data.FuelStationBrandsType
import com.albrivas.fuelpump.core.model.data.FuelType
import com.albrivas.fuelpump.core.model.data.PriceCategory
import com.albrivas.fuelpump.core.uikit.icon.FuelStationIcons
import com.albrivas.fuelpump.core.uikit.theme.PriceCheap
import com.albrivas.fuelpump.core.uikit.theme.PriceExpensive
import com.albrivas.fuelpump.core.uikit.theme.PriceNormal

fun FuelType.translation() =  when (this) {
        FuelType.GASOLINE_95 -> R.string.gasoline_95
        FuelType.GASOLINE_98 -> R.string.gasoline_98
        FuelType.DIESEL -> R.string.diesel
        FuelType.DIESEL_PLUS -> R.string.diesel_plus
        FuelType.ELECTRIC -> R.string.electric
    }

fun Int.toFuelType() = when (this) {
        R.string.gasoline_95 -> FuelType.GASOLINE_95
        R.string.gasoline_98 -> FuelType.GASOLINE_98
        R.string.diesel -> FuelType.DIESEL
        R.string.diesel_plus -> FuelType.DIESEL_PLUS
        R.string.electric -> FuelType.ELECTRIC
        else -> FuelType.GASOLINE_95
    }

fun FuelStationBrandsType.toBrandStationIcon() = when(this) {
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
        FuelStationBrandsType.UNKOWN -> FuelStationIcons.Uknown
}

fun PriceCategory.toColor() = when (this) {
        PriceCategory.CHEAP -> PriceCheap
        PriceCategory.NORMAL -> PriceNormal
        PriceCategory.EXPENSIVE -> PriceExpensive
}
