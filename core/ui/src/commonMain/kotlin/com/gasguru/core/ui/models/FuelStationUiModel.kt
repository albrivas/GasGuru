package com.gasguru.core.ui.models

import androidx.compose.runtime.Stable
import com.gasguru.core.model.data.FuelStation
import org.jetbrains.compose.resources.DrawableResource

@Stable
data class FuelStationUiModel(
    val fuelStation: FuelStation,
    val brandIcon: DrawableResource,
    val formattedDistance: String,
    val formattedDirection: String,
    val formattedName: String,
)
