package com.albrivas.fuelpump.feature.profile.ui

import com.albrivas.fuelpump.core.model.data.FuelType

sealed class ProfileEvents {
    data class Fuel(val fuel: FuelType) : ProfileEvents()
}
