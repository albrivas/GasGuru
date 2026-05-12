package com.gasguru.core.ui.mapper

import com.gasguru.core.model.data.FuelStationBrandsType
import com.gasguru.core.ui.models.FuelStationBrandsUiModel

fun FuelStationBrandsType.toUiModel(): FuelStationBrandsUiModel =
    FuelStationBrandsUiModel.ALL_BRANDS.first { it.type == this }
