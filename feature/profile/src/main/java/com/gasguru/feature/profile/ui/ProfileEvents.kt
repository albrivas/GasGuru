package com.gasguru.feature.profile.ui

import com.gasguru.core.model.data.FuelType
import com.gasguru.core.ui.ThemeModeUi

sealed class ProfileEvents {
    data class Fuel(val fuel: FuelType) : ProfileEvents()
    data class Theme(val theme: ThemeModeUi) : ProfileEvents()
}
