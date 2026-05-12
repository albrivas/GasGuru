package com.gasguru.core.ui.mapper

import androidx.compose.runtime.Composable
import com.gasguru.core.model.data.FuelType
import com.gasguru.core.ui.models.FuelTypeUiModel
import com.gasguru.core.uikit.components.fuel_list.FuelItemModel
import org.jetbrains.compose.resources.stringResource

fun FuelType.toUiModel(): FuelTypeUiModel =
    FuelTypeUiModel.ALL_FUELS.first { it.type == this }

@Composable
fun FuelTypeUiModel.toFuelItem(): FuelItemModel = FuelItemModel(
    iconRes = iconRes,
    nameRes = stringResource(resource = translationRes),
)
