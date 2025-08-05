package com.gasguru.auto.common

import androidx.car.app.CarContext
import com.gasguru.core.uikit.theme.DarkGasGuruColors
import com.gasguru.core.uikit.theme.GasGuruColors
import com.gasguru.core.uikit.theme.LightGasGuruColors

fun CarContext.getAutomotiveThemeColor(): GasGuruColors {
    return if (isDarkMode) DarkGasGuruColors else LightGasGuruColors
}