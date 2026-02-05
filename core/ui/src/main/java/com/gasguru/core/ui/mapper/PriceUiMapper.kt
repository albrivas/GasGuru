package com.gasguru.core.ui.mapper

import com.gasguru.core.model.data.FuelStation
import com.gasguru.core.model.data.FuelType
import com.gasguru.core.ui.models.PriceUiModel

/**
 * Maps [FuelType] and [FuelStation] to [PriceUiModel].
 *
 * @receiver FuelType domain model
 * @param fuelStation The fuel station containing price information
 * @return PriceUiModel UI representation
 */
fun FuelType.toPriceUiModel(fuelStation: FuelStation): PriceUiModel = PriceUiModel(
    rawPrice = extractPrice(fuelStation),
    fuelType = this,
)
