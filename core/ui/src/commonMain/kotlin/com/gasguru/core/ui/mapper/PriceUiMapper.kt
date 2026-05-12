package com.gasguru.core.ui.mapper

import com.gasguru.core.model.data.FuelStation
import com.gasguru.core.model.data.FuelType
import com.gasguru.core.ui.models.PriceUiModel

fun FuelType.toPriceUiModel(fuelStation: FuelStation): PriceUiModel = PriceUiModel(
    rawPrice = extractPrice(fuelStation),
    fuelType = this,
)
