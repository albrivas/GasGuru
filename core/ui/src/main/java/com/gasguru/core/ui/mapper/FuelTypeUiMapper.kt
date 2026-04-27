package com.gasguru.core.ui.mapper

import android.content.Context
import com.gasguru.core.model.data.FuelType
import com.gasguru.core.ui.models.FuelTypeUiModel
import com.gasguru.core.uikit.components.fuel_list.FuelItemModel

/**
 * Maps [FuelType] domain model to [FuelTypeUiModel].
 *
 * @receiver FuelType domain model
 * @return FuelTypeUiModel UI representation
 */
fun FuelType.toUiModel(): FuelTypeUiModel =
    FuelTypeUiModel.ALL_FUELS.first { it.type == this }

/**
 * Maps [FuelTypeUiModel] to [FuelItemModel] for UI representation.
 *
 * @receiver FuelTypeUiModel UI model
 * @param context Android context to resolve the string resource
 * @return FuelItemModel for composable UI components
 */
fun FuelTypeUiModel.toFuelItem(context: Context): FuelItemModel = FuelItemModel(
    iconRes = iconRes,
    nameRes = context.getString(translationRes),
)
