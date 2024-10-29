package com.gasguru.feature.profile.ui

import com.gasguru.core.model.data.FuelType

sealed class ProfileEvents {
    data class Fuel(val fuel: FuelType) : ProfileEvents()
}
