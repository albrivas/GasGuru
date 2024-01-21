package com.albrivas.fuelpump.core.ui

import com.albrivas.fuelpump.core.model.data.FuelType

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
