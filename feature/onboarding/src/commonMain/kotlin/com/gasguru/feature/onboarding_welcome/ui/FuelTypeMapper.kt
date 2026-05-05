package com.gasguru.feature.onboarding_welcome.ui

import com.gasguru.core.model.data.FuelType
import com.gasguru.core.ui.models.FuelTypeUiModel
import kotlinx.coroutines.runBlocking
import org.jetbrains.compose.resources.getString

internal fun String.toFuelType(): FuelType =
    FuelTypeUiModel.ALL_FUELS.firstOrNull {
        runBlocking { getString(it.translationRes) } == this
    }?.type ?: FuelType.GASOLINE_95
